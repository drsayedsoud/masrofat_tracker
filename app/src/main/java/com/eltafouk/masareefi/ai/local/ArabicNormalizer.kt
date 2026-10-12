package com.eltafouk.masareefi.ai.local

object ArabicNormalizer {

    private val PUNCT = Regex("[\\u060C\\u061B\\u061F\\u06D4.,!?;\"'()\\[\\]{}«»…\\-_/\\\\|*~`]+")

    /** Converts Eastern Arabic digits (٠-٩) and Persian (۰-۹) to Western digits (0-9). */
    fun digits(s: String): String {
        val sb = StringBuilder(s.length)
        for (ch in s) {
            sb.append(
                when (ch) {
                    in '٠'..'٩' -> '0' + (ch - '٠')
                    in '۰'..'۹' -> '0' + (ch - '۰')
                    else -> ch
                }
            )
        }
        return sb.toString()
    }

    /** Strips Arabic diacritics / tashkeel and tatweel. */
    fun stripDiacritics(s: String): String =
        s.replace(Regex("[\\u064B-\\u0652\\u0670\\u0640]"), "")

    /** Normalizes letters: unifies alef, yaa, taa marbouta. */
    fun letters(s: String): String = s
        .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا').replace('ٱ', 'ا')
        .replace('ى', 'ي').replace('ة', 'ه').replace('ؤ', 'و').replace('ئ', 'ي')

    fun normalize(text: String): String {
        var t = digits(text)
        t = stripDiacritics(t)
        t = letters(t)
        // Add spaces around numbers glued to letters
        t = t.replace(Regex("(?<=[\\p{IsArabic}])(?=\\d)"), " ")
            .replace(Regex("(?<=\\d)(?=[\\p{IsArabic}])"), " ")
        t = t.replace(PUNCT, " ")
        return t.trim().lowercase().replace(Regex("\\s+"), " ")
    }

    fun tokenize(text: String): List<String> {
        val norm = normalize(text)
        if (norm.isBlank()) return emptyList()
        return norm.split(" ")
    }
}
