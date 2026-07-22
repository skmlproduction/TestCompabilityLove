package dev.lovetest.app.ui.features

internal fun calculatorLetterChips(name1: String, name2: String): List<Char> {
    val combined = (name1 + name2).filter { it.isLetter() }
    return combined
        .map { it.uppercaseChar() }
        .distinct()
        .take(4)
        .ifEmpty { listOf('?') }
}

internal fun calculatorCommonLetters(name1: String, name2: String): List<Char> {
    val a = name1.filter { it.isLetter() }.lowercase().toSet()
    if (a.isEmpty()) return emptyList()
    return name2
        .filter { it.isLetter() }
        .map { it.lowercaseChar() }
        .distinct()
        .filter { it in a }
        .map { it.uppercaseChar() }
        .take(5)
}

internal fun lettersHighlightIndex(length: Int): Int {
    if (length <= 0) return -1
    return (length / 2).coerceIn(0, length - 1)
}
