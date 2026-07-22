package dev.lovetest.core.domain

/**
 * Validates entertainment-test name fields against the same letter rules as
 * [DefaultLoveScoreCalculator] normalize (Unicode letters only after trim).
 */
object NameInputValidator {

    fun isValidName(raw: String): Boolean {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return false
        return trimmed.any { it.isLetter() }
    }

    fun canSubmitPair(name1: String, name2: String): Boolean =
        isValidName(name1) && isValidName(name2)

    fun canSubmitSingle(name: String): Boolean = isValidName(name)
}
