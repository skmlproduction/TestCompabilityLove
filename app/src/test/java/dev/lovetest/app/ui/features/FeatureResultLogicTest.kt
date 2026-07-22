package dev.lovetest.app.ui.features

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureResultLogicTest {

    @Test
    fun lettersSecretCode_lubovSchast_inValidRange() {
        val code = lettersSecretCode("ЛЮБОВЬ", "СЧАСТЬ")
        assertTrue(code in 1..9)
        assertEquals(code, lettersSecretCode("ЛЮБОВЬ", "СЧАСТЬ"))
    }

    @Test
    fun lettersSecretCode_empty_returnsFallback() {
        assertEquals(7, lettersSecretCode("", ""))
    }

    @Test
    fun lettersHighlightIndex_middleLetter() {
        assertEquals(3, lettersHighlightIndex(6))
    }

    @Test
    fun calculatorCommonLetters_findsSharedLetters() {
        val common = calculatorCommonLetters("Anna", "Maria")
        assertTrue(common.contains('A'))
    }

    @Test
    fun calculatorLetterChips_distinctUpToFour() {
        val chips = calculatorLetterChips("Anna", "Max")
        assertEquals(4, chips.size)
        assertEquals('A', chips[0])
    }

    @Test
    fun calculatorLetterChips_emptyNames_fallbackQuestion() {
        assertEquals(listOf('?'), calculatorLetterChips("", ""))
    }
}
