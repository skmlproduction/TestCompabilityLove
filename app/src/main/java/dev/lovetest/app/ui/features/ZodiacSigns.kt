package dev.lovetest.app.ui.features

internal enum class ZodiacElement {
    Fire,
    Earth,
    Air,
    Water,
}

internal val zodiacSignElements: List<ZodiacElement> = listOf(
    ZodiacElement.Fire,
    ZodiacElement.Earth,
    ZodiacElement.Air,
    ZodiacElement.Water,
    ZodiacElement.Fire,
    ZodiacElement.Earth,
    ZodiacElement.Air,
    ZodiacElement.Water,
    ZodiacElement.Fire,
    ZodiacElement.Earth,
    ZodiacElement.Air,
    ZodiacElement.Water,
)

internal fun zodiacElementForIndex(index: Int): ZodiacElement =
    zodiacSignElements.getOrElse(index) { ZodiacElement.Fire }

internal fun zodiacElementForSignName(signName: String, allSigns: List<String>): ZodiacElement {
    val index = allSigns.indexOf(signName)
    return if (index >= 0) zodiacElementForIndex(index) else ZodiacElement.Fire
}
