package com.eltafouk.masareefi.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface SpeechState {
    data object Idle : SpeechState
    data class Listening(val rms: Float, val partial: String) : SpeechState
    data class Final(val text: String) : SpeechState
    data class Error(val code: Int, val message: String) : SpeechState
}

class SpeechInput(private val context: Context) {
    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val state: StateFlow<SpeechState> = _state.asStateFlow()

    private var recognizer: SpeechRecognizer? = null
    private var partialText = ""
    private var language = "ar-EG"
    private var triedFallback = false

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening() {
        destroy()
        partialText = ""
        triedFallback = false
        language = "ar-EG"
        listen()
    }

    private fun listen() {
        val r = runCatching { SpeechRecognizer.createSpeechRecognizer(context) }.getOrNull()
        if (r == null) {
            _state.value = SpeechState.Error(-1, "التعرف على الصوت غير متاح على هذا الجهاز")
            return
        }
        recognizer = r

        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _state.value = SpeechState.Listening(0f, "")
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {
                val normalizedRms = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                _state.value = SpeechState.Listening(normalizedRms, partialText)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                Log.w("MasareefiSpeech", "Speech error code: $error")
                val langIssue = error == SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED ||
                        error == SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE

                if (langIssue && !triedFallback) {
                    triedFallback = true
                    language = "ar"
                    destroy()
                    listen()
                    return
                }

                if (partialText.isNotBlank() && (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_NO_MATCH)) {
                    _state.value = SpeechState.Final(partialText)
                    return
                }

                val msg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "لم أسمع بوضوح، حاول مرة أخرى 🙏"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "لم يتم تسجيل صوت 🤔"
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "الإنترنت ضعيف، اكتب الجملة أو حاول ثانية"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "يرجى منح إذن الميكروفون"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "الميكروفون مشغول حالياً"
                    else -> "تعذر الاستماع، يرجى المحاولة مرة أخرى"
                }
                _state.value = SpeechState.Error(error, msg)
            }

            override fun onResults(results: Bundle?) {
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.trim().orEmpty()
                _state.value = if (text.isNotEmpty()) {
                    SpeechState.Final(text)
                } else if (partialText.isNotBlank()) {
                    SpeechState.Final(partialText)
                } else {
                    SpeechState.Error(SpeechRecognizer.ERROR_NO_MATCH, "لم أسمع بوضوح، حاول ثانية 🙏")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val text = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!text.isNullOrBlank()) {
                    partialText = text
                    val rms = (_state.value as? SpeechState.Listening)?.rms ?: 0f
                    _state.value = SpeechState.Listening(rms, partialText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1800L)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        try {
            _state.value = SpeechState.Listening(0f, "")
            r.startListening(intent)
        } catch (e: Exception) {
            _state.value = SpeechState.Error(-1, "تعذر تشغيل الميكروفون: ${e.message}")
        }
    }

    fun stop() {
        runCatching { recognizer?.stopListening() }
    }

    fun reset() {
        destroy()
        _state.value = SpeechState.Idle
    }

    fun destroy() {
        runCatching { recognizer?.cancel() }
        runCatching { recognizer?.destroy() }
        recognizer = null
    }
}
