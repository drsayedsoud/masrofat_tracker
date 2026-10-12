package com.eltafouk.masareefi.ai.local

import com.eltafouk.masareefi.data.db.TransactionType

data class ParsedTransactionResult(
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val note: String,
    val source: String,
    val success: Boolean,
    val errorMessage: String? = null
)

object ExpenseRuleParser {

    private val INCOME_TRIGGERS = listOf(
        "دخل", "مرتب", "راتب", "قبضت", "استلمت", "جالي", "مكافاه", "مكافأة", "حافز",
        "ارباح", "ربح", "ايداع", "تحويل لي", "جمعيه", "جمعية", "بيع", "فلوس جاتني",
        "عموله", "عمولة", "شغل حر", "فريلانس"
    )

    private val EXPENSE_TRIGGERS = listOf(
        "صرفت", "مصروف", "مصاريف", "دفعت", "شريت", "اشتريت", "فاتوره", "فاتورة",
        "سحبت", "خرجت", "دفعه", "دفعة", "قسط", "دين"
    )

    private val FOOD_WORDS = listOf(
        "قهوه", "قهوة", "شاي", "اكل", "طعام", "غدا", "غداء", "فطار", "عشا", "عشاء",
        "مطعم", "كافيه", "سوبرماركت", "خضار", "فاكهه", "فاكهة", "لحمه", "لحمة",
        "فراخ", "عصير", "حلويات", "بقاله", "بقالة", "ساندوتش", "بيتزا", "كشري", "عيش", "لبن"
    )

    private val TRANSPORT_WORDS = listOf(
        "بنزين", "مواصلات", "اوبر", "تاكسي", "مترو", "كارته", "كارتة", "بنزينه",
        "بنزينة", "اتوبيس", "ميكروباص", "تذكره", "تذكرة", "زيت عربيه", "غسيل عربيه", "ركنه", "باركينج"
    )

    private val BILLS_WORDS = listOf(
        "ايجار", "كهربا", "كهرباء", "مايه", "مياه", "غاز", "نت", "انترنت", "شحن",
        "رصيد", "فاتوره", "فاتورة", "صيانه", "صيانة", "بواب", "تليفون", "ارضي"
    )

    private val HEALTH_WORDS = listOf(
        "كشف", "دكتور", "طبيب", "علاج", "دواء", "صيدليه", "صيدلية", "مستشفي",
        "مستشفى", "تحاليل", "اشعه", "اشعة", "عياده", "عيادة", "اسنان", "نظاره", "نظارة"
    )

    private val SHOPPING_WORDS = listOf(
        "هدوم", "لبس", "جزمة", "جزمه", "شنطه", "شنطة", "شراء", "شوبينج", "هديه", "هدية",
        "سوق", "عطور", "اكسسوارات", "موبايل", "ساعه", "ساعة"
    )

    private val ENTERTAINMENT_WORDS = listOf(
        "خروجه", "خروجة", "سينما", "جيم", "اشتراك", "فسحه", "فسحة", "العاب", "بلايستيشن", "حلاقه", "حلاقة"
    )

    private val COMMITMENTS_WORDS = listOf(
        "جمعيه", "جمعية", "قسط", "دين", "سداد", "سلفه", "سلفة", "قرض"
    )

    private val INCOME_SALARY_WORDS = listOf(
        "مرتب", "راتب", "شغل", "فريلانس", "حافز", "مكافاه", "مكافأة", "عموله", "عمولة", "ارباح", "ربح"
    )

    fun parse(rawText: String): ParsedTransactionResult {
        val trimmed = rawText.trim()
        if (trimmed.isEmpty()) {
            return ParsedTransactionResult(
                type = TransactionType.EXPENSE,
                amount = 0.0,
                category = "أخرى",
                note = "",
                source = "محلي (أوفلاين)",
                success = false,
                errorMessage = "النص فارغ"
            )
        }

        val tokens = ArabicNormalizer.tokenize(trimmed)
        if (tokens.isEmpty()) {
            return ParsedTransactionResult(
                type = TransactionType.EXPENSE,
                amount = 0.0,
                category = "أخرى",
                note = trimmed,
                source = "محلي (أوفلاين)",
                success = false,
                errorMessage = "لم يتم التعرف على كلمات"
            )
        }

        // 1. Find Amount
        val numberMatch = ArabicNumbers.findFirstNumber(tokens)
        val amount = numberMatch?.first ?: 0.0

        if (amount <= 0.0) {
            return ParsedTransactionResult(
                type = TransactionType.EXPENSE,
                amount = 0.0,
                category = "أخرى",
                note = trimmed,
                source = "محلي (أوفلاين)",
                success = false,
                errorMessage = "لم يتم تحديد المبلغ"
            )
        }

        // 2. Detect Type (Income vs Expense)
        var hasIncomeTrigger = false
        var hasExpenseTrigger = false

        for (token in tokens) {
            if (INCOME_TRIGGERS.any { token.contains(it) }) hasIncomeTrigger = true
            if (EXPENSE_TRIGGERS.any { token.contains(it) }) hasExpenseTrigger = true
        }

        // If explicitly "دخل" or "قبضت" or "مرتب" -> Income
        val type = if (hasIncomeTrigger && !hasExpenseTrigger) {
            TransactionType.INCOME
        } else if (hasExpenseTrigger) {
            TransactionType.EXPENSE
        } else {
            // Default check based on context words
            if (tokens.any { t -> INCOME_SALARY_WORDS.any { t.contains(it) } }) {
                TransactionType.INCOME
            } else {
                TransactionType.EXPENSE
            }
        }

        // 3. Detect Category
        val category = when {
            type == TransactionType.INCOME -> {
                when {
                    tokens.any { t -> COMMITMENTS_WORDS.any { t.contains(it) } } -> "جمعيات والتزامات"
                    tokens.any { t -> INCOME_SALARY_WORDS.any { t.contains(it) } } -> "راتب وعمل"
                    else -> "دخل عام"
                }
            }
            tokens.any { t -> FOOD_WORDS.any { t.contains(it) } } -> "طعام وشراب"
            tokens.any { t -> TRANSPORT_WORDS.any { t.contains(it) } } -> "مواصلات"
            tokens.any { t -> BILLS_WORDS.any { t.contains(it) } } -> "فواتير وسكن"
            tokens.any { t -> HEALTH_WORDS.any { t.contains(it) } } -> "صحة وعلاج"
            tokens.any { t -> SHOPPING_WORDS.any { t.contains(it) } } -> "تسوق وملابس"
            tokens.any { t -> ENTERTAINMENT_WORDS.any { t.contains(it) } } -> "ترفيه ورياضة"
            tokens.any { t -> COMMITMENTS_WORDS.any { t.contains(it) } } -> "أقساط والتزامات"
            else -> "مصروف عام"
        }

        // 4. Clean Note / Description
        // Filter out common command words like "ضيفلي", "سجل", "احسب", "جنيه", and the parsed numbers
        val stopWords = setOf(
            "ضيفلي", "ضيف", "سجل", "سجلي", "احسب", "حط", "اكتب", "جنيه", "جنية", "ج", "ج.م",
            "دخل", "مصروف", "مصاريف", "ب", "بـ", "من", "في", "على", "ع"
        )
        val noteWords = mutableListOf<String>()
        val consumedIndices = numberMatch?.second ?: IntRange.EMPTY

        for (i in tokens.indices) {
            if (i in consumedIndices) continue
            val t = tokens[i]
            if (t in stopWords) continue
            noteWords.add(t)
        }

        val note = if (noteWords.isNotEmpty()) {
            noteWords.joinToString(" ")
        } else {
            if (type == TransactionType.INCOME) "دخل إضافي" else category
        }

        return ParsedTransactionResult(
            type = type,
            amount = amount,
            category = category,
            note = note,
            source = "محلي (أوفلاين)",
            success = true
        )
    }
}
