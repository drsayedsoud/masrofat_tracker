package com.eltafouk.masareefi.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eltafouk.masareefi.ai.ExpenseInterpreter
import com.eltafouk.masareefi.ai.local.ParsedTransactionResult
import com.eltafouk.masareefi.data.db.MasareefiDatabase
import com.eltafouk.masareefi.data.db.TransactionEntity
import com.eltafouk.masareefi.data.db.TransactionType
import com.eltafouk.masareefi.data.settings.SettingsRepository
import com.eltafouk.masareefi.voice.SpeechInput
import com.eltafouk.masareefi.voice.SpeechState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MasareefiViewModel(app: Application) : AndroidViewModel(app) {

    private val db = MasareefiDatabase.getInstance(app)
    private val dao = db.transactionDao()
    private val settingsRepo = SettingsRepository(app)
    private val interpreter = ExpenseInterpreter(app, settingsRepo)
    private val speechInput = SpeechInput(app)

    val allTransactions: StateFlow<List<TransactionEntity>> = dao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeTransactions: StateFlow<List<TransactionEntity>> = dao.getTransactionsByType(TransactionType.INCOME)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseTransactions: StateFlow<List<TransactionEntity>> = dao.getTransactionsByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Double> = dao.getTotalAmountByType(TransactionType.INCOME)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = dao.getTotalAmountByType(TransactionType.EXPENSE)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val apiKey: StateFlow<String> = settingsRepo.geminiApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val selectedModel: StateFlow<String> = settingsRepo.selectedModel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gemini-2.5-flash")

    val forceOffline: StateFlow<Boolean> = settingsRepo.forceOffline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val speechState: StateFlow<SpeechState> = speechInput.state

    private val _parsedSpeechResult = MutableStateFlow<ParsedTransactionResult?>(null)
    val parsedSpeechResult: StateFlow<ParsedTransactionResult?> = _parsedSpeechResult.asStateFlow()

    private val _isProcessingAI = MutableStateFlow(false)
    val isProcessingAI: StateFlow<Boolean> = _isProcessingAI.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    fun isOnline(): Boolean = interpreter.isOnline()

    init {
        // Monitor voice final speech
        viewModelScope.launch {
            speechInput.state.collect { state ->
                if (state is SpeechState.Final && state.text.isNotBlank()) {
                    processVoiceText(state.text)
                }
            }
        }
    }

    private fun processVoiceText(text: String) {
        viewModelScope.launch {
            _isProcessingAI.value = true
            val res = interpreter.parseInput(text)
            _isProcessingAI.value = false
            if (res.success && res.amount > 0) {
                addTransaction(res.type, res.amount, res.category, res.note)
                val typeName = if (res.type == TransactionType.INCOME) "دخل" else "مصروف"
                _snackBarMessage.value = "تمت إضافة $typeName: ${res.category} بمبلغ ${res.amount} تلقائياً ✅"
            } else {
                _snackBarMessage.value = res.errorMessage ?: "لم أتمكن من استخراج المبلغ، حاول ثانية"
            }
            clearSpeechDialog()
        }
    }

    fun processQuickTextInput(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isProcessingAI.value = true
            val res = interpreter.parseInput(text)
            _isProcessingAI.value = false
            if (res.success && res.amount > 0) {
                addTransaction(res.type, res.amount, res.category, res.note)
                _snackBarMessage.value = "تمت إضافة ${res.category} بمبلغ ${res.amount} (${res.source})"
            } else {
                _snackBarMessage.value = res.errorMessage ?: "تعذر فهم الأمر، يرجى كتابة المبلغ بوضوح"
            }
        }
    }

    fun addTransaction(type: TransactionType, amount: Double, category: String, note: String) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                type = type,
                amount = amount,
                category = category,
                note = note,
                timestamp = System.currentTimeMillis()
            )
            dao.insertTransaction(entity)
            clearSpeechDialog()
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            dao.deleteById(id)
            _snackBarMessage.value = "تم حذف المعاملة"
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            dao.deleteAll()
            _snackBarMessage.value = "تم مسح جميع البيانات وإعادة الضبط بنجاح ✅"
        }
    }

    fun startListening() {
        _parsedSpeechResult.value = null
        speechInput.startListening()
    }

    fun stopListening() {
        speechInput.stop()
    }

    fun clearSpeechDialog() {
        speechInput.reset()
        _parsedSpeechResult.value = null
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            settingsRepo.setGeminiApiKey(key)
        }
    }

    fun saveModel(model: String) {
        viewModelScope.launch {
            settingsRepo.setSelectedModel(model)
        }
    }

    fun saveForceOffline(force: Boolean) {
        viewModelScope.launch {
            settingsRepo.setForceOffline(force)
        }
    }

    suspend fun testGeminiKey(key: String): Result<Boolean> {
        return interpreter.testGeminiKey(key)
    }

    override fun onCleared() {
        super.onCleared()
        speechInput.destroy()
    }
}
