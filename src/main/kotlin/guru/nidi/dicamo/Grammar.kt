package guru.nidi.dicamo

import org.slf4j.LoggerFactory
import java.text.Normalizer

val log = LoggerFactory.getLogger("grammar")

private val VERB_ENDINGS = mapOf(
    listOf("ar") to setOf(
        "ant",
        "at", "ada", "ats", "ades",
        "o", "es", "a", "em", "eu", "en",
        "ava", "aves", "ava", "avem", "aveu", "aven",
        "i", "ares", "a", "arem", "areu", "aren",
        "are", "aras", "ara", "arem", "areu", "aran",
        "i", "is", "i", "em", "eu", "in",
        "es", "essis", "es", "essim", "essiu", "essin",
        "aria", "aries", "aria", "ariem", "arieu", "arien",
        "a", "i", "em", "eu", "in"
    ),
    listOf("er", "re") to setOf(
        "ent",
        "ut", "uda", "uts", "udes",
        "o", "s", "", "em", "eu", "en",
        "ia", "ies", "ia", "iem", "ieu", "ien",
        "i", "eres", "e", "erem", "ereu", "eren",
        "ere", "eras", "era", "erem", "ereu", "eran",
        "i", "is", "i", "em", "eu", "in",
        "es", "essis", "es", "essim", "essiu", "essin",
        "eria", "eries", "eria", "eriem", "erieu", "erien",
        "", "i", "em", "eu", "in"
    ),
    listOf("ir") to setOf(
        "int",
        "it", "ida", "its", "ides",
        "o", "s", "", "im", "iu", "en",
        "eixo", "eixes", "eix", "eixen",
        "ia", "ies", "ia", "iem", "ieu", "ien",
        "i", "ires", "i", "irem", "ireu", "iren",
        "ire", "iras", "ira", "irem", "ireu", "iran",
        "i", "is", "i", "im", "iu", "in",
        "eixi", "eixis", "eixi", "eixin",
        "is", "issis", "is", "issim", "issiu", "issin",
        "iria", "iries", "iria", "iriem", "irieu", "irien",
        "", "i", "im", "iu", "in",
        "eix", "eixi", "eixin"
    )
)

private val IRREGULAR_VERBS = mapOf(
    "anar" to setOf(
        "vaig", "vas", "va", "van",
        "anire", "aniras", "anira", "anirem", "anireu", "aniran",
        "vagi", "vagis", "vagi", "vagin",
        "aniria", "aniries", "aniria", "aniriem", "anirieu", "anirien",
        "ves", "vagi", "vagin"
    ),
    "estar" to setOf(
        "estic", "estas", "estan",
        "estigui", "estigueres", "estigue", "estiguerem", "estiguereu", "estigueren",
        "estigui", "estiguis", "estigui", "estiguem", "estigueu", "estiguin",
        "estigues", "estiguessis", "estigues", "estiguessim", "estiguessiu", "estiguessin",
        "estigues", "estigui", "estiguem", "estigueu", "estiguin"
    )
)

fun infinitivesOf(word: String): List<String> {
    val normalized = word.normalize()
    val irregular = IRREGULAR_VERBS.filter { (_, forms) -> word in forms }
    if (irregular.isNotEmpty()) return irregular.keys.toList()

    val infs = VERB_ENDINGS.flatMap { (infEndings, endings) ->
        endings
            .filter { ending -> normalized.endsWith(ending) }
            .maxByOrNull { it.length }
            ?.let { longestEnding ->
                infEndings.map { infEnding ->
                    normalized.replaceEnding(longestEnding, infEnding)
                }
            }
            ?: listOf()
    }
    log.debug("Infinitives of $word: $infs")
    return infs
}

private fun String.replaceEnding(oldEnding: String, newEnding: String): String {
    val base = this.dropLast(oldEnding.length)
    val startChangesToAOU = startChangesToAOU(oldEnding, newEnding)
    return when {
        base.endsWith("qu") && startChangesToAOU -> base.dropLast(2) + "c"
        base.endsWith("c") && startChangesToAOU -> base.dropLast(1) + "ç"
        base.endsWith("g") && startChangesToAOU -> base.dropLast(1) + "j"
        base.endsWith("gu") && startChangesToAOU -> base.dropLast(2) + "g"
        else -> base
    } + newEnding
}

private fun startChangesToAOU(from: String, to: String) = from.startsWithEI() && !to.startsWithEI()

private fun String.startsWithEI() = startsWith("e") || startsWith("i")

fun String.normalize() =
    Regex("\\p{InCombiningDiacriticalMarks}+").replace(Normalizer.normalize(this, Normalizer.Form.NFKD), "")
