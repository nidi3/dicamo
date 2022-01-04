package guru.nidi.dicamo

import java.text.Normalizer

fun String.endPronoun() = when {
    endsWith("-se") -> "-se"
    endsWith("'s") -> "'s"
    else -> ""
}

fun String.pronounLess() = dropLast(endPronoun().length)

fun String.ending() = pronounLess().takeLast(2)

fun String.normalize() =
    Regex("\\p{InCombiningDiacriticalMarks}+").replace(Normalizer.normalize(this, Normalizer.Form.NFKD), "")

fun String.frontToBack() = when {
    endsWith("qu") -> listOf(this, dropLast(2) + "c") //qüe -> qua, que -> ca
    endsWith("c") -> listOf(dropLast(1) + "ç") //ce -> ça
    endsWith("g") -> listOf(dropLast(1) + "j") //ge -> ja
    endsWith("gu") -> listOf(this, dropLast(2) + "g") //güe -> gua, gue -> ga
    else -> listOf(this)
}

fun String.backToFront() = when {
    endsWith("j") -> listOf(dropLast(1) + "g") //jo -> gi
    endsWith("ç") -> listOf(dropLast(1) + "c") //ço -> ce
    else -> listOf(this)
}

fun String.startsWithBack() = isEmpty() || first() in "aou"
fun String.startsWithFront() = isNotEmpty() && first() in "ei"

fun String.replaceEnd(old: String, new: String): List<String> =
    if (!endsWith(old)) listOf()
    else listOf(this.dropLast(old.length) + new)

fun String.replaceEnd(old: String, then: (String) -> List<String>): List<String> =
    if (!endsWith(old)) listOf()
    else then(this.dropLast(old.length))


