package guru.nidi.dicamo

import guru.nidi.dicamo.Form.*
import org.slf4j.LoggerFactory
import java.text.Normalizer
import kotlin.math.max
import kotlin.math.min

//for verb conjugations, see
//https://ca.wiktionary.org/wiki/Viccionari:Conjugaci%C3%B3_en_catal%C3%A0/paradigmes

val log = LoggerFactory.getLogger("grammar")

private val VERB_TYPES = listOf("ar", "er", "re", "dre", "ndre", "ur", "ure")

private enum class Form { GERUNDI, PARTICIPI, PRESENT, IMPERFET, SIMPLE, FUTUR, SUB_PRESENT, SUB_IMPERFET, CONDICIONAL, IMPERATIU }

private val VERB_ENDINGS = mapOf(
    listOf("ar") to mapOf(
        "" to mapOf(
            GERUNDI to listOf("ant"),
            PARTICIPI to listOf("at", "ada", "ats", "ades"),
            PRESENT to listOf("o", "es", "a", "em", "eu", "en"),
            IMPERFET to listOf("ava", "aves", "ava", "àvem", "àveu", "aven"),
            SIMPLE to listOf("í", "ares", "à", "àrem", "àreu", "aren"),
            FUTUR to listOf("aré", "aràs", "arà", "arem", "areu", "aran"),
            SUB_PRESENT to listOf("i", "is", "i", "em", "eu", "in"),
            SUB_IMPERFET to listOf("és", "essis", "és", "éssim", "éssiu", "essin"),
            CONDICIONAL to listOf("aria", "aries", "aria", "aríem", "aríeu", "arien"),
            IMPERATIU to listOf("a", "i", "em", "eu", "in")
        )
    ),
    listOf("er", "re") to mapOf(
        "" to mapOf(
            GERUNDI to listOf("ent"),
            PARTICIPI to listOf("ut", "uda", "uts", "udes"),
            PRESENT to listOf("o", "s", "", "em", "eu", "en"),
            IMPERFET to listOf("ia", "ies", "ia", "íem", "íeu", "ien"),
            SIMPLE to listOf("í", "eres", "é", "érem", "éreu", "eren"),
            FUTUR to listOf("eré", "eràs", "erà", "erem", "ereu", "eran"),
            SUB_PRESENT to listOf("i", "is", "i", "em", "eu", "in"),
            SUB_IMPERFET to listOf("és", "essis", "és", "éssim", "éssiu", "essin"),
            CONDICIONAL to listOf("eria", "eries", "eria", "eríem", "eríeu", "erien"),
            IMPERATIU to listOf("", "i", "em", "eu", "in")
        )
    ),
    listOf("ir") to mapOf(
        "" to mapOf(
            GERUNDI to listOf("int"),
            PARTICIPI to listOf("it", "ida", "its", "ides"),
            PRESENT to listOf("o", "s", "", "im", "iu", "en"),
            IMPERFET to listOf("ia", "ies", "ia", "íem", "íeu", "ien"),
            SIMPLE to listOf("í", "ires", "í", "írem", "íreu", "iren"),
            FUTUR to listOf("iré", "iràs", "irà", "irem", "ireu", "iran"),
            SUB_PRESENT to listOf("i", "is", "i", "im", "iu", "in"),
            SUB_IMPERFET to listOf("ís", "issis", "ís", "íssim", "íssiu", "issin"),
            CONDICIONAL to listOf("iria", "iries", "iria", "iríem", "iríeu", "irien"),
            IMPERATIU to listOf("", "i", "im", "iu", "in"),
        ),
        "incoatius" to mapOf(
            PRESENT to listOf("eixo", "eixes", "eix", "", "", "eixen"),
            SUB_PRESENT to listOf("eixi", "eixis", "eixi", "", "", "eixin"),
            IMPERATIU to listOf("eix", "eixi", "", "", "eixin")
        ),
        "ompl" to mapOf(
            PRESENT to listOf("", "es", "e"),
            IMPERATIU to listOf("e"),
        ),
        "-ompl" to mapOf(
            PARTICIPI to listOf("", "", "", "", "ert", "erta", "erts", "ertes")
        )
    ),
)

private val IRREGULAR_VERBS = mapOf(
    "anar" to listOf(
        "vaig", "vas", "va", "van",
        "anire", "aniras", "anira", "anirem", "anireu", "aniran",
        "vagi", "vagis", "vagi", "vagin",
        "aniria", "aniries", "aniria", "aniriem", "anirieu", "anirien",
        "ves", "vagi", "vagin"
    ),
    "estar" to listOf(
        "estic", "estas", "estan",
        "estigui", "estigueres", "estigue", "estiguerem", "estiguereu", "estigueren",
        "estigui", "estiguis", "estigui", "estiguem", "estigueu", "estiguin",
        "estigues", "estiguessis", "estigues", "estiguessim", "estiguessiu", "estiguessin",
        "estigues", "estigui", "estiguem", "estigueu", "estiguin"
    ),
    "ser" to listOf(
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
    "fer" to listOf(
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
    "poder" to listOf(
        "pogut", "poguda", "poguts", "pogudes",
        "puc", "pots", "pot",
        "pogui", "pogueres", "pogue", "poguerem", "poguereu", "pogueren",
        "podre", "podras", "podra", "podrem", "podreu", "podran",
        "pugui", "puguis", "pugui", "puguem", "pugueu", "puguin",
        "pogues", "poguessis", "pogues", "poguessim", "poguessiu", "poguessin",
        "podria", "podries", "podria", "podriem", "podrieu", "podrien",
        "pugues", "pugui", "puguem", "pugueu", "puguin"
    ),
    "saber" to listOf(
        "se", "saps", "sap",
        "sabre", "sabras", "sabra", "sabrem", "sabreu", "sabran",
        "sapiga", "sapigues", "sapiga", "sapiguem", "sapigueu", "sapiguen",
        "sabria", "sabries", "sabria", "sabriem", "sabrieu", "sabrien",
        "sapigues", "sapiga", "sapiguem", "sapigueu", "sapiguen"
    ),
    "tenir" to listOf(
        "tingut", "tinguda", "tinguts", "tingudes",
        "tinc", "te",
        "tingui", "tingueres", "tingue", "tinguerem", "tinguereu", "tingueren",
        "tindre", "tindras", "tindra", "tindrem", "tindreu", "tindran",
        "tingui", "tinguis", "tingui", "tinguem", "tingueu", "tinguin",
        "tingues", "tinguessis", "tingues", "tinguessim", "tinguessiu", "tinguessin",
        "tindria", "tindries", "tindria", "tindriem", "tindrieu", "tindrien",
        "ten", "te", "tingues", "tingui", "tinguem", "tingueu", "tinguin"
    ),
    "haver" to listOf(
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

private val effectiveEndings: Map<List<String>, Map<String, Set<String>>> =
    VERB_ENDINGS.entries.associate { (infEndings, groups) ->
        infEndings to groups.entries.associate { (name, group) ->
            name to groups[""]!!.flatMap { (form, defaultForms) ->
                val forms = group[form]?.map { it.ifEmpty { null } } ?: listOf()
                (defaultForms.indices union forms.indices).map { index ->
                    (forms.getOrNull(index) ?: defaultForms[index]).normalize()
                }
            }.toSet()
        }
    }

private infix fun IntRange.union(other: IntRange) = IntRange(min(first, other.first), max(last, other.last))

private fun belongsToGroup(base: String, group: String): Boolean {
    if (group == "" || group == "incoatius") return true //TODO use list of incoatius verbs?
    return (group.first() == '-' && base.endsWith(group.drop(1))) || group == base
}

fun infinitivesOf(word: String): List<String> {
    val normalized = word.normalize()
    val irregular = IRREGULAR_VERBS.filter { (_, forms) -> word in forms }
    if (irregular.isNotEmpty()) return irregular.keys.toList()

    val infs = effectiveEndings.flatMap { (infEndings, groups) ->
        groups.flatMap { (name, group) ->
            group
                .filter { ending -> normalized.endsWith(ending) }
                .maxByOrNull { ending -> ending.length }
                ?.let { longestEnding ->
                    val base = word.dropLast(longestEnding.length)
                    if (!belongsToGroup(base, name)) listOf()
                    else infEndings.flatMap { infEnding ->
                        base.replaceEnding(longestEnding, infEnding)
                    }
                }
                ?.filterNot { inf -> inf in VERB_TYPES }
                ?: listOf()
        }.toSet()
    }
    log.debug("Infinitives of $word: $infs")
    return infs
}

internal fun String.replaceEnding(oldEnding: String, newEnding: String): List<String> {
    val newBase = when (newEnding) {
        "ar" -> when {
            oldEnding.startsWith("i") && dropLastWhile { it == 'i' }.last().isVowel() ->
                listOf(this + "i") //esglais -> esglaiar
            oldEnding.startsWithFront() -> frontToBack()
            else -> listOf(this)
        }
        "ir" -> when {
            (endsWith("cull") || endsWith("surt") || endsWith("cup")) &&
                    oldEnding in listOf("o", "s", "", "i", "en", "is", "in") ->
                listOfNotNull(
                    if (oldEnding == "i") this else null,
                    replaceLast("u", "o")
                ) //cullo -> collir, culli -> cullir/collir
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
    else -> listOf(this)
}

private fun String.replaceLast(find: String, replaceWith: String): String {
    val pos = lastIndexOf(find)
    return replaceRange(pos, pos + find.length, replaceWith)
}

private fun Char.isVowel() = this in "aeiou"
private fun String.startsWithBack() = isNotEmpty() && first() in "aou"
private fun String.startsWithFront() = isNotEmpty() && first() in "ei"

fun String.normalize() =
    Regex("\\p{InCombiningDiacriticalMarks}+").replace(Normalizer.normalize(this, Normalizer.Form.NFKD), "")
