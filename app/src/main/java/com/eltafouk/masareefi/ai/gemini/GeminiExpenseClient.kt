package com.eltafouk.masareefi.ai.gemini

import android.util.Log
import com.eltafouk.masareefi.ai.local.ParsedTransactionResult
import com.eltafouk.masareefi.data.db.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class GeminiExpenseClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
) {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun parseWithGemini(
        apiKey: String,
        model: String,
        userText: String
    ): ParsedTransactionResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext ParsedTransactionResult(
                type = TransactionType.EXPENSE,
                amount = 0.0,
                category = "أخرى",
                note = userText,
                source = "ذكاء اصطناعي (Gemini)",
                success = false,
                errorMessage = "مفتاح الذكاء الاصطناعي غير متوفر"
            )
        }

        val systemInstruction = """
            أنت مساعد مالي ذكي لتطبيق متتبع مصاريف ودخل باللغة العربية والعامية المصرية.
            وظيفتك تحليل الجملة واستخراج البيانات التالية بدقة بصيغة JSON فقط دون أي نصوص إضافية:
            {
              "type": "INCOME" أو "EXPENSE",
              "amount": رقم عشري أو صحيح للمبلغ (مثال: 50.0 أو 5000.0),
              "category": اسم التصنيف (مثل: طعام وشراب، مواصلات، فواتير وسكن، صحة وعلاج، تسوق وملابس، ترفيه، راتب وعمل، جمعيات والتزامات، أخرى),
              "note": وصف مختصر للعملية (مثل: قهوة، سوبرماركت، مرتب أكتوبر، بنزين، كشف دكتور)
            }
            إذا كان النص يحتوي على كلمة دخل أو مرتب أو قبضت أو مكافأة اجعل type = "INCOME".
            إذا كان النص يحتوي على صرفت أو مصروف أو دفعت أو شريت اجعل type = "EXPENSE".
        """.trimIndent()

        val requestPayload = buildJsonObject {
            putJsonObject("systemInstruction") {
                putJsonArray("parts") {
                    add(buildJsonObject { put("text", systemInstruction) })
                }
            }
            putJsonArray("contents") {
                add(buildJsonObject {
                    put("role", "user")
                    putJsonArray("parts") {
                        add(buildJsonObject { put("text", userText) })
                    }
                })
            }
            putJsonObject("generationConfig") {
                put("responseMimeType", "application/json")
                put("temperature", 0.1)
            }
        }.toString()

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"

        val request = Request.Builder()
            .url(url)
            .header("x-goog-api-key", apiKey)
            .post(requestPayload.toRequestBody(jsonMediaType))
            .build()

        try {
            val responseText = executeRequest(request)
            val jsonRoot = json.parseToJsonElement(responseText).jsonObject

            val candidates = jsonRoot["candidates"]?.jsonArray
            val firstCandidate = candidates?.firstOrNull()?.jsonObject
            val content = firstCandidate?.get("content")?.jsonObject
            val parts = content?.get("parts")?.jsonArray
            val textPart = parts?.firstOrNull()?.jsonObject?.get("text")?.jsonPrimitive?.content

            if (textPart.isNullOrBlank()) {
                return@withContext ParsedTransactionResult(
                    type = TransactionType.EXPENSE,
                    amount = 0.0,
                    category = "أخرى",
                    note = userText,
                    source = "ذكاء اصطناعي (Gemini)",
                    success = false,
                    errorMessage = "استجابة فارغة من النموذج"
                )
            }

            // Parse clean JSON output
            val parsedJson = json.parseToJsonElement(textPart).jsonObject
            val typeStr = parsedJson["type"]?.jsonPrimitive?.content ?: "EXPENSE"
            val type = if (typeStr.equals("INCOME", ignoreCase = true)) TransactionType.INCOME else TransactionType.EXPENSE
            val amount = parsedJson["amount"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val category = parsedJson["category"]?.jsonPrimitive?.content ?: "أخرى"
            val note = parsedJson["note"]?.jsonPrimitive?.content ?: userText

            ParsedTransactionResult(
                type = type,
                amount = amount,
                category = category,
                note = note,
                source = "ذكاء اصطناعي ($model)",
                success = amount > 0,
                errorMessage = if (amount <= 0) "لم يتم تحديد المبلغ" else null
            )
        } catch (e: Exception) {
            Log.e("MasareefiAI", "Gemini error: ${e.message}", e)
            ParsedTransactionResult(
                type = TransactionType.EXPENSE,
                amount = 0.0,
                category = "أخرى",
                note = userText,
                source = "ذكاء اصطناعي (Gemini)",
                success = false,
                errorMessage = e.message ?: "فشل الاتصال بالذكاء الاصطناعي"
            )
        }
    }

    suspend fun testApiKey(apiKey: String, model: String = "gemini-2.5-flash"): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model"
            val request = Request.Builder()
                .url(url)
                .header("x-goog-api-key", apiKey.trim())
                .get()
                .build()

            val callResult = executeRequest(request)
            if (callResult.contains("name") || callResult.contains("models/")) {
                Result.success(true)
            } else {
                Result.failure(Exception("مفتاح غير صالح أو استجابة غير متوقعة"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun executeRequest(req: Request): String = suspendCancellableCoroutine { cont ->
        val call = client.newCall(req)
        cont.invokeOnCancellation { runCatching { call.cancel() } }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (cont.isActive) cont.resumeWith(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val body = it.body?.string().orEmpty()
                        if (cont.isActive) cont.resume(body)
                    } else {
                        val errorText = it.body?.string().orEmpty()
                        if (cont.isActive) cont.resumeWith(Result.failure(IOException("HTTP ${it.code}: $errorText")))
                    }
                }
            }
        })
    }
}
