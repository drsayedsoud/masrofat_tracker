package com.eltafouk.masareefi.ai

import com.eltafouk.masareefi.ai.local.ArabicNormalizer
import com.eltafouk.masareefi.ai.local.ArabicNumbers
import com.eltafouk.masareefi.ai.local.ExpenseRuleParser
import com.eltafouk.masareefi.data.db.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseRuleParserTest {

    @Test
    fun testArabicDigitsNormalization() {
        val converted = ArabicNormalizer.digits("٥٠٠٠")
        assertEquals("5000", converted)
    }

    @Test
    fun testIncomeParsing_Digits() {
        val result = ExpenseRuleParser.parse("ضيفلي دخل 5000 مرتب")
        assertTrue(result.success)
        assertEquals(TransactionType.INCOME, result.type)
        assertEquals(5000.0, result.amount, 0.01)
        assertEquals("راتب وعمل", result.category)
    }

    @Test
    fun testIncomeParsing_ArabicDigits() {
        val result = ExpenseRuleParser.parse("قبضت الجمعية ٤٠٠٠")
        assertTrue(result.success)
        assertEquals(TransactionType.INCOME, result.type)
        assertEquals(4000.0, result.amount, 0.01)
        assertEquals("جمعيات والتزامات", result.category)
    }

    @Test
    fun testExpenseParsing_Coffee() {
        val result = ExpenseRuleParser.parse("صرفت 50 قهوة")
        assertTrue(result.success)
        assertEquals(TransactionType.EXPENSE, result.type)
        assertEquals(50.0, result.amount, 0.01)
        assertEquals("طعام وشراب", result.category)
    }

    @Test
    fun testExpenseParsing_Supermarket() {
        val result = ExpenseRuleParser.parse("سجل مصروف 120 سوبرماركت")
        assertTrue(result.success)
        assertEquals(TransactionType.EXPENSE, result.type)
        assertEquals(120.0, result.amount, 0.01)
        assertEquals("طعام وشراب", result.category)
    }

    @Test
    fun testExpenseParsing_Bills() {
        val result = ExpenseRuleParser.parse("دفعت فاتورة الكهربا 350 جنيه")
        assertTrue(result.success)
        assertEquals(TransactionType.EXPENSE, result.type)
        assertEquals(350.0, result.amount, 0.01)
        assertEquals("فواتير وسكن", result.category)
    }

    @Test
    fun testExpenseParsing_SpokenNumbers() {
        val result = ExpenseRuleParser.parse("صرفت خمسين جنيه بنزين")
        assertTrue(result.success)
        assertEquals(TransactionType.EXPENSE, result.type)
        assertEquals(50.0, result.amount, 0.01)
        assertEquals("مواصلات", result.category)
    }
}
