package guru.nidi.dicamo

import org.slf4j.LoggerFactory
import java.text.Normalizer
import kotlin.math.max
import kotlin.math.min

val log = LoggerFactory.getLogger("grammar")

private val VERB_TYPES = listOf(
    "ar", "car", "iar", "jar", "uar",
    "er",
    "re", "bre", "dre", "ndre", "ur", "ure",
    "ir", "gir", "rir"
)

private class FlatEnding(val possibleBase: (String) -> Boolean, val groups: Map<String, Set<String>>)

private val effectiveEndings: Map<String, FlatEnding> =
    verbs.entries.associate { (infEnding, ending) ->
        infEnding to FlatEnding(ending.possibleBase, ending.groups
            .flatMap { (name, group) -> name.split(",").map { it to group } }
            .associate { (name, group) ->
                val addForDefault = if ("/" in name) name.substring(name.indexOf("/") * 2 + 1) else ""
                name to ending.defaultGroup.flatMap { (form, defaultForms) ->
                    val forms = group[form] ?: listOf()
                    (defaultForms.indices union forms.indices).map { index ->
                        ((forms.getOrNull(index) ?: (addForDefault + defaultForms[index]))).normalize()
                    }
                }.toSet()
            })
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
    val infs = effectiveEndings.flatMap { (infEnding, ending) ->
        ending.groups.flatMap { (name, group) ->
            group
                .filter { ending -> normalized.endsWith(ending) }
                .maxByOrNull { ending -> ending.length }
                ?.let { longestEnding ->
                    val base = baseInGroup(normalized.dropLast(longestEnding.length), name)
                    if (base == null || !ending.possibleBase(base)) null
                    else base.replaceEnding(longestEnding, infEnding)
                }
                ?.filterNot { inf -> inf in VERB_TYPES }
                ?: listOf()
        }.toSet()
    }.flatMap { addDiacritics(it) }
    log.debug("Infinitives of $word: $infs")
    return infs
}

val diacritics = listOf(
    "aixer" to "àixer", "anyer" to "ànyer",
    "coneixer" to "conèixer", "creixer" to "créixer", "reixer" to "rèixer", "neixer" to "néixer", "peixer" to "péixer",
    "enyer" to "ènyer", "encer" to "èncer", "emer" to "émer",
    "aitzar" to "aïtzar", "eitzar" to "eïtzar",
    "orrer" to "órrer", "orcer" to "òrcer", "omer" to "òmer",
    "umer" to "úmer", "unyer" to "únyer",
)

private fun addDiacritics(s: String): List<String> {
    // ï and ç are not completely handled
    diacritics
        .firstOrNull { s.endsWith(it.first) }
        ?.let { return listOf(s.dropLast(it.first.length) + it.second) }
    if (s.endsWith("car")) return listOf(s, s.dropLast(3) + "çar")
    return listOf(s)
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
