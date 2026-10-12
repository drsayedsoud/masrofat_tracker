package com.eltafouk.masareefi.ai.local

object ArabicNumbers {

    val UNITS: Map<String, Double> = mapOf(
        "صفر" to 0.0,
        "واحد" to 1.0, "واحده" to 1.0, "وحده" to 1.0, "الواحد" to 1.0, "الواحده" to 1.0,
        "اتنين" to 2.0, "اثنين" to 2.0, "اتنان" to 2.0, "اثنان" to 2.0,
        "تلاته" to 3.0, "ثلاثه" to 3.0, "تلات" to 3.0, "ثلاث" to 3.0,
        "اربعه" to 4.0, "اربع" to 4.0,
        "خمسه" to 5.0, "خمس" to 5.0,
        "سته" to 6.0, "ست" to 6.0,
        "سبعه" to 7.0, "سبع" to 7.0,
        "تمانيه" to 8.0, "ثمانيه" to 8.0, "تمن" to 8.0, "ثمان" to 8.0,
        "تسعه" to 9.0, "تسع" to 9.0,
        "عشره" to 10.0, "عشر" to 10.0,
        "حداشر" to 11.0, "احداشر" to 11.0, "احد عشر" to 11.0,
        "اتناشر" to 12.0, "اثناشر" to 12.0, "اثنا عشر" to 12.0,
        "تلتاشر" to 13.0, "ثلتاشر" to 13.0, "ثلاثة عشر" to 13.0,
        "اربعتاشر" to 14.0, "اربعة عشر" to 14.0,
        "خمستاشر" to 15.0, "خمسة عشر" to 15.0,
        "ستاشر" to 16.0, "ستة عشر" to 16.0,
        "سبعتاشر" to 17.0, "سبعة عشر" to 17.0,
        "تمنتاشر" to 18.0, "ثمانية عشر" to 18.0,
        "تسعتاشر" to 19.0, "تسعة عشر" to 19.0,
        "عشرين" to 20.0,
        "تلاتين" to 30.0, "ثلاثين" to 30.0,
        "اربعين" to 40.0,
        "خمسين" to 50.0,
        "ستين" to 60.0,
        "سبعين" to 70.0,
        "تمانين" to 80.0, "ثمانين" to 80.0,
        "تسعين" to 90.0,
        "ميه" to 100.0, "مية" to 100.0, "مائه" to 100.0, "مائة" to 100.0, "ميتين" to 200.0, "مائتين" to 200.0,
        "تلتوميه" to 300.0, "تلتومية" to 300.0, "ثلاثمائه" to 300.0, "ثلاثمائة" to 300.0,
        "اربعميه" to 400.0, "اربعمية" to 400.0, "اربعمائه" to 400.0, "اربعمائة" to 400.0,
        "خمسميه" to 500.0, "خمسمية" to 500.0, "خمسمائه" to 500.0, "خمسمائة" to 500.0,
        "ستميه" to 600.0, "ستمية" to 600.0, "ستمائه" to 600.0, "ستمائة" to 600.0,
        "سبعميه" to 700.0, "سبعمية" to 700.0, "سبعمائه" to 700.0, "سبعمائة" to 700.0,
        "تمنميه" to 800.0, "تمنمية" to 800.0, "ثمانمائه" to 800.0, "ثمانمائة" to 800.0,
        "تسعميه" to 900.0, "تسعمية" to 900.0, "تسعمائه" to 900.0, "تسعمائة" to 900.0,
        "الف" to 1000.0, "الفين" to 2000.0,
        "تلاتلاف" to 3000.0, "تلات الاف" to 3000.0, "ثلاثة الاف" to 3000.0,
        "اربع تلاف" to 4000.0, "اربعة الاف" to 4000.0,
        "خمس تلاف" to 5000.0, "خمسة الاف" to 5000.0,
        "ست تلاف" to 6000.0, "ستة الاف" to 6000.0,
        "سبع تلاف" to 7000.0, "سبعة الاف" to 7000.0,
        "تمن تلاف" to 8000.0, "ثمانية الاف" to 8000.0,
        "تسع تلاف" to 9000.0, "تسعة الاف" to 9000.0,
        "عشر تلاف" to 10000.0, "عشرة الاف" to 10000.0,
        "مليون" to 1000000.0, "مليونين" to 2000000.0,
        "نص" to 0.5, "نصف" to 0.5,
        "ربع" to 0.25
    )

    data class ParsedNum(val value: Double, val consumedWords: Int)

    /**
     * Extracts an amount from tokens starting at index [start].
     * Can parse digits ("150", "25.5") or Arabic word numbers ("خمسين", "مية وخمسين", "الفين وخمسمية").
     */
    fun extractNumber(tokens: List<String>, start: Int): ParsedNum? {
        if (start !in tokens.indices) return null
        val token = tokens[start]

        // 1. Direct number (e.g. "150", "30.5")
        token.toDoubleOrNull()?.let { return ParsedNum(it, 1) }

        // 2. Direct unit match
        var currentVal = UNITS[token] ?: return null
        var consumed = 1
        var idx = start + 1

        while (idx < tokens.size) {
            val next = tokens[idx]
            // Conjunction "و" e.g. ["خمسين", "و", "خمسة"] or ["مية", "وخمسين"]
            if (next == "و" && idx + 1 < tokens.size) {
                val afterWaw = tokens[idx + 1]
                val v = UNITS[afterWaw] ?: afterWaw.toDoubleOrNull()
                if (v != null) {
                    currentVal += v
                    consumed += 2
                    idx += 2
                    continue
                }
            } else if (next.startsWith("و") && next.length > 1) {
                val sub = next.substring(1)
                val v = UNITS[sub] ?: sub.toDoubleOrNull()
                if (v != null) {
                    currentVal += v
                    consumed += 1
                    idx += 1
                    continue
                }
            } else if (next == "الاف" || next == "الف") {
                if (currentVal < 1000) {
                    currentVal *= 1000
                    consumed += 1
                    idx += 1
                    continue
                }
            } else if (next == "مليون" || next == "ملايين") {
                if (currentVal < 1000000) {
                    currentVal *= 1000000
                    consumed += 1
                    idx += 1
                    continue
                }
            }
            break
        }

        return ParsedNum(currentVal, consumed)
    }

    /**
     * Finds the first number in the text and returns its value and indices.
     */
    fun findFirstNumber(tokens: List<String>): Pair<Double, IntRange>? {
        for (i in tokens.indices) {
            val parsed = extractNumber(tokens, i)
            if (parsed != null && parsed.value > 0) {
                return Pair(parsed.value, i until (i + parsed.consumedWords))
            }
        }
        return null
    }
}
