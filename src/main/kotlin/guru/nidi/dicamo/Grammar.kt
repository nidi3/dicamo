package guru.nidi.dicamo

import org.slf4j.LoggerFactory
import java.text.Normalizer
import kotlin.math.max
import kotlin.math.min

val log = LoggerFactory.getLogger("grammar")

private val VERB_TYPES = listOf("ar", "er", "re", "dre", "ndre", "ur", "ure")

private val effectiveEndings: Map<String, Map<String, Set<String>>> =
    verbs.entries.associate { (infEndings, groups) ->
        infEndings to groups
            .flatMap { (name, group) -> name.split(",").map { it to group } }
            .associate { (name, group) ->
                val addForDefault = if ("/" in name) name.substring(name.indexOf("/") * 2 + 1) else ""
                name to groups[""]!!.flatMap { (form, defaultForms) ->
                    val forms = group[form] ?: listOf()
                    (defaultForms.indices union forms.indices).map { index ->
                        ((forms.getOrNull(index) ?: (addForDefault + defaultForms[index]))).normalize()
                    }
                }.toSet()
            }
    }

private infix fun IntRange.union(other: IntRange) = IntRange(min(first, other.first), max(last, other.last))

private fun baseInGroup(base: String, group: String): String? {
    val fromBase = group.substringBefore("/")
    val toBase = group.substringAfter("/")
    if (group == "") return base
    if (group == "incoatius") return base //TODO use list of incoatius verbs?
    if (fromBase == base)
        return base.dropLast(fromBase.length) + toBase
    if (fromBase.firstOrNull() == '-' && base.endsWith(fromBase.drop(1)))
        return base.dropLast(fromBase.length - 1) + toBase.drop(1)
    return null
}

fun infinitivesOf(word: String): List<String> {
    val normalized = word.normalize()
    val infs = effectiveEndings.flatMap { (infEnding, groups) ->
        groups.flatMap { (name, group) ->
            group
                .filter { ending -> normalized.endsWith(ending) }
                .maxByOrNull { ending -> ending.length }
                ?.let { longestEnding ->
                    baseInGroup(normalized.dropLast(longestEnding.length), name)
                        ?.replaceEnding(longestEnding, infEnding)
                }
                ?.filterNot { inf -> inf in VERB_TYPES }
                ?: listOf()
        }.toSet()
    }.map { addUmlaut(it) }
    log.debug("Infinitives of $word: $infs")
    return infs
}

private fun addUmlaut(s: String): String {
    //TODO not finished, or diacritic insensitive search?
    if (s.endsWith("orrer")) return s.dropLast(5)+"órrer"
    return s
}

internal fun String.replaceEnding(oldEnding: String, newEnding: String): List<String> {
    val newBase = when (newEnding) {
        "ar" -> when {
            oldEnding.startsWithFront() -> frontToBack()
            else -> listOf(this)
        }
        "ir", "er" -> when {
            oldEnding.startsWithBack() -> backToFront()
            else -> listOf(this)
        }
        else -> listOf(this)
    }
    return newBase.map { it + newEnding }
}

private fun String.frontToBack() = when {
    endsWith("qu") -> listOf(this, dropLast(2) + "c") //qüe -> qua, que -> ca
    endsWith("c") -> listOf(dropLast(1) + "ç") //ce -> ça
    endsWith("g") -> listOf(dropLast(1) + "j") //ge -> ja
    endsWith("gu") -> listOf(this, dropLast(2) + "g") //güe -> gua, gue -> ga
    else -> listOf(this)
}

private fun String.backToFront() = when {
    endsWith("j") -> listOf(dropLast(1) + "g") //jo -> gi
    endsWith("ç") -> listOf(dropLast(1) + "c") //ço -> ce
    else -> listOf(this)
}

private fun String.startsWithBack() = isEmpty() || first() in "aou"
private fun String.startsWithFront() = isNotEmpty() && first() in "ei"

fun String.normalize() =
    Regex("\\p{InCombiningDiacriticalMarks}+").replace(Normalizer.normalize(this, Normalizer.Form.NFKD), "")
