package dev.lovetest.core.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NameInputValidatorTest {

    @Test
    fun valid_letterNames() {
        assertTrue(NameInputValidator.isValidName("Anna"))
        assertTrue(NameInputValidator.isValidName("Анна"))
        assertTrue(NameInputValidator.isValidName("  Max  "))
        assertTrue(NameInputValidator.isValidName("Jean-Luc"))
        assertTrue(NameInputValidator.isValidName("O'Brien"))
    }

    @Test
    fun invalid_blankOrDigitsOnly() {
        assertFalse(NameInputValidator.isValidName(""))
        assertFalse(NameInputValidator.isValidName("   "))
        assertFalse(NameInputValidator.isValidName("123"))
        assertFalse(NameInputValidator.isValidName("!!!"))
        assertFalse(NameInputValidator.isValidName("---"))
    }

    @Test
    fun pair_requiresBothValid() {
        assertTrue(NameInputValidator.canSubmitPair("Anna", "Max"))
        assertFalse(NameInputValidator.canSubmitPair("Anna", "123"))
        assertFalse(NameInputValidator.canSubmitPair("   ", "Max"))
    }

    @Test
    fun single_requiresLetters() {
        assertTrue(NameInputValidator.canSubmitSingle("Victory"))
        assertFalse(NameInputValidator.canSubmitSingle("42"))
    }
}
