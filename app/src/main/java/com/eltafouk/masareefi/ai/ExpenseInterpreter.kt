package com.eltafouk.masareefi.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.eltafouk.masareefi.ai.gemini.GeminiExpenseClient
import com.eltafouk.masareefi.ai.local.ExpenseRuleParser
import com.eltafouk.masareefi.ai.local.ParsedTransactionResult
import com.eltafouk.masareefi.data.settings.SettingsRepository
import kotlinx.coroutines.flow.first

class ExpenseInterpreter(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val geminiClient: GeminiExpenseClient = GeminiExpenseClient()
) {

    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val active = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(active) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun parseInput(userText: String): ParsedTransactionResult {
        val apiKey = settingsRepository.geminiApiKey.first()
        val model = settingsRepository.selectedModel.first()
        val forceOffline = settingsRepository.forceOffline.first()
        val online = isOnline()

        // 1. Try Gemini if online, key exists, and not forced offline
        if (!forceOffline && online && apiKey.isNotBlank()) {
            val geminiResult = geminiClient.parseWithGemini(apiKey, model, userText)
            if (geminiResult.success && geminiResult.amount > 0) {
                return geminiResult
            }
        }

        // 2. Offline / local fallback rule parser
        val localResult = ExpenseRuleParser.parse(userText)
        return localResult
    }

    suspend fun testGeminiKey(apiKey: String): Result<Boolean> {
        val model = settingsRepository.selectedModel.first()
        return geminiClient.testApiKey(apiKey, model)
    }
}
