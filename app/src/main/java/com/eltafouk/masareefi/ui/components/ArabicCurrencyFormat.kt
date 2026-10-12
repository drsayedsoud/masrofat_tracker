package com.eltafouk.masareefi.ui.components

import com.eltafouk.masareefi.ai.local.ArabicNormalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ArabicCurrencyFormat {

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale("ar"))
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))

    /**
     * Formats money amounts as a natural integer string without decimal points
     * or thousands separator commas (e.g. 4000), using English digits.
     */
    fun formatMoney(amount: Double): String {
        val rounded = kotlin.math.round(amount).toLong()
        return ArabicNormalizer.digits(rounded.toString())
    }

    /**
     * Formats timestamp into Arabic date/time with English digits.
     */
    fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date(now))
        val itemStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date(timestamp))

        val formatted = if (todayStr == itemStr) {
            "اليوم ${timeFormat.format(Date(timestamp))}"
        } else if (diff < 2 * 86400000L) {
            "أمس ${timeFormat.format(Date(timestamp))}"
        } else {
            "${dateFormat.format(Date(timestamp))} - ${timeFormat.format(Date(timestamp))}"
        }
        return ArabicNormalizer.digits(formatted)
    }
}
