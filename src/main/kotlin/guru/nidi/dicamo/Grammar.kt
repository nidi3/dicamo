package guru.nidi.dicamo

import org.slf4j.LoggerFactory
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

private fun baseInGroup(base: String, ending: String, group: String): String? {
    val type = VerbList.verbs[base + ending]?.type ?: Type.UNKNOWN
    val fromBase = group.substringBefore("/")
    val toBase = group.substringAfter("/")
    return when {
        group == "" -> if (type != Type.INCOATIU) base else null
        group == "incoatiu" -> if (type != Type.PUR) base else null
        fromBase == base -> base.dropLast(fromBase.length) + toBase
        fromBase.firstOrNull() == '-' && base.endsWith(fromBase.drop(1)) ->
            base.dropLast(fromBase.length - 1) + toBase.drop(1)
        else -> null
    }
}

fun infinitivesOf(word: String): Pair<List<String>, List<String>> {
    val pronounLess = word.substringBeforeLast('-').substringBeforeLast('\'')
    val baseInfs = baseInfinitivesOf(pronounLess)
    val infs = baseInfs.mapNotNull { VerbList.verbs[it]?.name }
    log.debug("Infinitives of '$word': $infs / $baseInfs")
    return Pair(infs, baseInfs.flatMap { addDiacritics(it) })
}

private fun baseInfinitivesOf(word: String): List<String> {
    val normalized = word.normalize()
    val infs = effectiveEndings.flatMap { (infEnding, ending) ->
        ending.groups.flatMap { (name, group) ->
            group
                .filter { ending -> normalized.endsWith(ending) }
                .maxByOrNull { ending -> ending.length }
                ?.let { longestEnding ->
                    val base = baseInGroup(normalized.dropLast(longestEnding.length), infEnding, name)
                    if (base == null || !ending.possibleBase(base)) null
                    else base.replaceEnding(longestEnding, infEnding)
                }
                ?.filterNot { inf -> inf in VERB_TYPES }
                ?: listOf()
        }.toSet()
    }
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
