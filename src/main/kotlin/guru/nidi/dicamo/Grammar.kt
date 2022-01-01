package guru.nidi.dicamo

import org.slf4j.LoggerFactory
import java.text.Normalizer

val log = LoggerFactory.getLogger("grammar")

private val VERB_TYPES = listOf("ar", "er", "re", "dre", "ndre", "ur", "ure")

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
    ),
    "ser" to setOf(
        "sent", "essent",
        "estat", "estada", "estats", "estades", "sigut", "siguda", "siguts", "sigudes",
        "soc", "ets", "es", "som", "sou", "son",
        "era", "eres", "era", "erem", "ereu", "eren",
        "fui", "fores", "fou", "forem", "foreu", "foren",
        "sigui", "siguis", "sigui", "siguem", "sigueu", "siguin",
        "fos", "fossis", "fos", "fossim", "fossiu", "fossin",
        "seria", "series", "seria", "seriem", "serieu", "serien", "fora", "fores", "fora", "forem", "foreu", "foren",
        "sigues", "sigui", "siguem", "sigueu", "siguin"
    ),
    "fer" to setOf(
        "fet", "feta", "fets", "fetes",
        "faig", "fas", "fa", "fan",
        "feia", "feies", "feia", "feiem", "feieu", "feien",
        "fiu", "feu",
        "fare", "faras", "fara", "farem", "fareu", "faran",
        "faci", "facis", "faci", "facin",
        "fes",
        "faria", "faries", "faria", "fariem", "farieu", "farien",
        "fes", "faci", "facin"
    ),
    "poder" to setOf(
        "pogut", "poguda", "poguts", "pogudes",
        "puc", "pots", "pot",
        "pogui", "pogueres", "pogue", "poguerem", "poguereu", "pogueren",
        "podre", "podras", "podra", "podrem", "podreu", "podran",
        "pugui", "puguis", "pugui", "puguem", "pugueu", "puguin",
        "pogues", "poguessis", "pogues", "poguessim", "poguessiu", "poguessin",
        "podria", "podries", "podria", "podriem", "podrieu", "podrien",
        "pugues", "pugui", "puguem", "pugueu", "puguin"
    ),
    "saber" to setOf(
        "se", "saps", "sap",
        "sabre", "sabras", "sabra", "sabrem", "sabreu", "sabran",
        "sapiga", "sapigues", "sapiga", "sapiguem", "sapigueu", "sapiguen",
        "sabria", "sabries", "sabria", "sabriem", "sabrieu", "sabrien",
        "sapigues", "sapiga", "sapiguem", "sapigueu", "sapiguen"
    ),
    "tenir" to setOf(
        "tingut", "tinguda", "tinguts", "tingudes",
        "tinc", "te",
        "tingui", "tingueres", "tingue", "tinguerem", "tinguereu", "tingueren",
        "tindre", "tindras", "tindra", "tindrem", "tindreu", "tindran",
        "tingui", "tinguis", "tingui", "tinguem", "tingueu", "tinguin",
        "tingues", "tinguessis", "tingues", "tinguessim", "tinguessiu", "tinguessin",
        "tindria", "tindries", "tindria", "tindriem", "tindrieu", "tindrien",
        "ten", "te", "tingues", "tingui", "tinguem", "tingueu", "tinguin"
    ),
    "haver" to setOf(
        "hagut", "haguda", "haguts", "hagudes",
        "he", "haig", "has", "ha", "hem", "heu", "han",
        "hagui", "hagueres", "hague", "haguerem", "haguereu", "hagueren",
        "haure", "hauras", "haura", "haurem", "haureu", "hauran",
        "hagi", "hagis", "hagi", "hagim", "haguem", "hagiu", "hagueu", "hagin",
        "hagues", "haguessis", "hagues", "haguessim", "haguessiu", "haguessin",
        "haguesses", "haguessem", "haguesseu", "haguessen",
        "hauria", "hauries", "hauria", "hauriem", "haurieu", "haurien",
        "haguera", "hagueras", "haguera", "haguerem", "haguereu", "hagueren"
    )
)

fun infinitivesOf(word: String): List<String> {
    val normalized = word.normalize()
    val irregular = IRREGULAR_VERBS.filter { (_, forms) -> word in forms }
    if (irregular.isNotEmpty()) return irregular.keys.toList()

    val infs = VERB_ENDINGS.flatMap { (infEndings, endings) ->
        endings
            .filter { ending -> normalized.endsWith(ending) }
            .maxByOrNull { ending -> ending.length }
            ?.let { longestEnding ->
                infEndings.flatMap { infEnding ->
                    normalized.replaceEnding(longestEnding, infEnding)
                }
            }
            ?.filterNot { inf -> inf in VERB_TYPES }
            ?: listOf()
    }
    log.debug("Infinitives of $word: $infs")
    return infs
}

internal fun String.replaceEnding(oldEnding: String, newEnding: String): List<String> {
    val base = this.dropLast(oldEnding.length)
    if (newEnding == "ar") {
        if (oldEnding.startsWith("i") && base.dropLastWhile { it == 'i' }.last().isVowel())
            return listOf(base + "iar") //esglais -> esglaiar
        if (oldEnding.startsWithFront()) return base.frontToBack().map { it + newEnding }
    }
    return listOf(base + newEnding)
}

private fun String.frontToBack() = when {
    endsWith("qu") -> listOf(this, dropLast(2) + "c") //qüe -> quar, que -> car
    endsWith("c") -> listOf(dropLast(1) + "ç") //ce -> çar
    endsWith("g") -> listOf(dropLast(1) + "j") //ge -> jar
    endsWith("gu") -> listOf(this, dropLast(2) + "g") //güe -> guar, gue -> gar
    else -> listOf(this)
}

private fun Char.isVowel() = this in "aeiou"
private fun String.startsWithBack() = isNotEmpty() && first() in "aou"
private fun String.startsWithFront() = isNotEmpty() && first() in "ei"

fun String.normalize() =
    Regex("\\p{InCombiningDiacriticalMarks}+").replace(Normalizer.normalize(this, Normalizer.Form.NFKD), "")
