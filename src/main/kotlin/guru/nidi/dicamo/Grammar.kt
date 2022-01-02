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

private enum class Form {
    GERUNDI, PARTICIPI, PRESENT, IMPERFET, SIMPLE, FUTUR, SUB_PRESENT, SUB_IMPERFET, CONDICIONAL, IMPERATIU;

    operator fun invoke(vararg forms: String?) = this to listOf(*forms)
}

private fun ending(ending: String, vararg groups: Pair<String, Map<Form, List<String?>>>) =
    listOf(ending) to mapOf(*groups)

private fun ending(ending1: String, ending2: String, vararg groups: Pair<String, Map<Form, List<String?>>>) =
    listOf(ending1, ending2) to mapOf(*groups)

private fun group(vararg forms: Pair<Form, List<String?>>) = group("", *forms)

private fun group(name: String = "", vararg forms: Pair<Form, List<String?>>) = name to mapOf(*forms)

private val VERB_ENDINGS = mapOf(
    ending(
        "ar",
        group(
            GERUNDI("ant"),
            PARTICIPI("at", "ada", "ats", "ades"),
            PRESENT("o", "es", "a", "em", "eu", "en"),
            IMPERFET("ava", "aves", "ava", "àvem", "àveu", "aven"),
            SIMPLE("í", "ares", "à", "àrem", "àreu", "aren"),
            FUTUR("aré", "aràs", "arà", "arem", "areu", "aran"),
            SUB_PRESENT("i", "is", "i", "em", "eu", "in"),
            SUB_IMPERFET("és", "essis", "és", "éssim", "éssiu", "essin"),
            CONDICIONAL("aria", "aries", "aria", "aríem", "aríeu", "arien"),
            IMPERATIU("a", "i", "em", "eu", "in")
        ),
        group(
            "anar",
            PRESENT("vaig", "vas", "va", null, null, "van"),
            FUTUR("anire", "aniras", "anira", "anirem", "anireu", "aniran"),
            SUB_PRESENT("vagi", "vagis", "vagi", null, null, "vagin"),
            CONDICIONAL("aniria", "aniries", "aniria", "aniriem", "anirieu", "anirien"),
            IMPERATIU("ves", "vagi", null, null, "vagin")
        ),
        group(
            "estar",
            PRESENT("estic", "estas", null, null, null, "estan"),
            SIMPLE("estigui", "estigueres", "estigue", "estiguerem", "estiguereu", "estigueren"),
            SUB_PRESENT("estigui", "estiguis", "estigui", "estiguem", "estigueu", "estiguin"),
            SUB_IMPERFET("estigues", "estiguessis", "estigues", "estiguessim", "estiguessiu", "estiguessin"),
            IMPERATIU("estigues", "estigui", "estiguem", "estigueu", "estiguin")
        ),
        group(
            "-a/-ai",
            SIMPLE("i"),
            SUB_PRESENT("i", "is", "i", null, null, "in"),
            IMPERATIU(null, "i", null, null, "ain")
        ),
    ),
    ending(
        "er", "re",
        group(
            GERUNDI("ent"),
            PARTICIPI("ut", "uda", "uts", "udes"),
            PRESENT("o", "s", "", "em", "eu", "en"),
            IMPERFET("ia", "ies", "ia", "íem", "íeu", "ien"),
            SIMPLE("í", "eres", "é", "érem", "éreu", "eren"),
            FUTUR("eré", "eràs", "erà", "erem", "ereu", "eran"),
            SUB_PRESENT("i", "is", "i", "em", "eu", "in"),
            SUB_IMPERFET("és", "essis", "és", "éssim", "éssiu", "essin"),
            CONDICIONAL("eria", "eries", "eria", "eríem", "eríeu", "erien"),
            IMPERATIU("", "i", "em", "eu", "in")
        )
    ),
    ending(
        "ir",
        group(
            GERUNDI("int"),
            PARTICIPI("it", "ida", "its", "ides"),
            PRESENT("o", "s", "", "im", "iu", "en"),
            IMPERFET("ia", "ies", "ia", "íem", "íeu", "ien"),
            SIMPLE("í", "ires", "í", "írem", "íreu", "iren"),
            FUTUR("iré", "iràs", "irà", "irem", "ireu", "iran"),
            SUB_PRESENT("i", "is", "i", "im", "iu", "in"),
            SUB_IMPERFET("ís", "issis", "ís", "íssim", "íssiu", "issin"),
            CONDICIONAL("iria", "iries", "iria", "iríem", "iríeu", "irien"),
            IMPERATIU("", "i", "im", "iu", "in"),
        ),
        group(
            "incoatius",
            PRESENT("eixo", "eixes", "eix", null, null, "eixen"),
            SUB_PRESENT("eixi", "eixis", "eixi", null, null, "eixin"),
            IMPERATIU("eix", "eixi", null, null, "eixin")
        ),
        group(
            "-cull/-coll,-surt/-sort,-cup/-cop",
            PRESENT("o", "s", "", null, null, "en"),
            SUB_PRESENT("i", "is", "i", null, null, "in"),
            IMPERATIU("", "i", null, null, "in")
        ),
        group(
            "-cus/-cos",
            PRESENT("o", "es", "", null, null, "en"),
            SUB_PRESENT("i", "is", "i", null, null, "in"),
            IMPERATIU("", "i", null, null, "in")
        ),
        group(
            "-tus/-toss",
            PRESENT("so", "ses", "", null, null, "sen"),
            SUB_PRESENT("si", "sis", "si", null, null, "sin"),
            IMPERATIU("", "si", null, null, "sin")
        ),
        group(
            "ompl,desompl,reompl",
            PARTICIPI(null, null, null, null, "ert", "erta", "erts", "ertes"),
            PRESENT(null, "es", "e"),
            IMPERATIU("e"),
        ),
        group(
            "acompl,compl,incompl,recompl,supl",
            PARTICIPI(null, null, null, null, "ert", "erta", "erts", "ertes")
        ),
        group(
            "-impr/-imprim",
            PARTICIPI("es", "esa", "esos", "eses")
        ),
        group(
            "ob/obr",
            PRESENT(null, "res", "re"),
            IMPERATIU("re"),
        ),
        group(
            "-ob/-obr",
            PARTICIPI("ert", "erta", "erts", "ertes")
        ),
        group(
            "-mor",
            PARTICIPI("t", "ta", "ts", "tes")
        ),
        group(
            "-t/-ten",
            PARTICIPI("ingut", "inguda", "inguts", "ingudes"),
            PRESENT("inc", null, "e"),
            SIMPLE("ingui", "ingueres", "ingue", "inguerem", "inguereu", "ingueren"),
            FUTUR("indre", "indras", "indra", "indrem", "indreu", "indran"),
            SUB_PRESENT("ingui", "inguis", "ingui", "inguem", "ingueu", "inguin"),
            SUB_IMPERFET("ingues", "inguessis", "ingues", "inguessim", "inguessiu", "inguessin"),
            CONDICIONAL("indria", "indries", "indria", "indriem", "indrieu", "indrien"),
            IMPERATIU("en", "e", "ingues", "ingui", "inguem", "eniu", "ingueu", "inguin")
        ),
        group(
            "-v/-ven",
            PARTICIPI("ingut", "inguda", "inguts", "ingudes"),
            PRESENT("inc", null, "e"),
            SIMPLE("ingui", "ingueres", "ingue", "inguerem", "inguereu", "ingueren"),
            FUTUR("indre", "indras", "indra", "indrem", "indreu", "indran"),
            SUB_PRESENT("ingui", "inguis", "ingui", "inguem", "ingueu", "inguin"),
            SUB_IMPERFET("ingues", "inguessis", "ingues", "inguessim", "inguessiu", "inguessin"),
            CONDICIONAL("indria", "indries", "indria", "indriem", "indrieu", "indrien"),
            IMPERATIU("ine", "ingui", "inguem", "eniu", "inguin")
        ),
        group(
            "-llu",
            PRESENT(null, null, null, null, null, null, "u", "us", "u"),
            SUB_IMPERFET(null, null, null, null, null, null, "isses", "íssem", "ísseu", "issen"),
            IMPERATIU(null, null, null, null, null, "u"),
        ),
        group(
            "/eix",
            PRESENT("ixo", "ixes", "ix", null, null, "ixen", "isc"),
            SUB_PRESENT("ixi", "ixis", "ixi", null, null, "ixin"),
            IMPERATIU("ix", "ixi", null, null, "ixin")
        ),
        group(
            "re/reeix,des/deseix,sobre/sobreeix,sota/sotaeix",
            PRESENT("ixo", "ixes", "ix", null, null, "ixen"),
            SUB_PRESENT("ixi", "ixis", "ixi", null, null, "ixin"),
            IMPERATIU("ix", "ixi", null, null, "ixin")
        ),
        group(
            "pu/pud",
            PRESENT(null, "ts", "t"),
            SUB_IMPERFET(null, null, null, null, null, null, "isses", "íssem", "ísseu", "issen"),
            IMPERATIU("t"),
        ),
    ),
)

private val IRREGULAR_VERBS = mapOf(
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
    val irregular = IRREGULAR_VERBS.filter { (_, forms) -> word in forms }
    if (irregular.isNotEmpty()) return irregular.keys.toList()

    val infs = effectiveEndings.flatMap { (infEndings, groups) ->
        groups.flatMap { (name, group) ->
            group
                .filter { ending -> normalized.endsWith(ending) }
                .maxByOrNull { ending -> ending.length }
                ?.let { longestEnding ->
                    baseInGroup(word.dropLast(longestEnding.length), name)?.let { base ->
                        infEndings.flatMap { infEnding ->
                            base.replaceEnding(longestEnding, infEnding)
                        }
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
            oldEnding.startsWithFront() -> frontToBack()
            else -> listOf(this)
        }
        "ir" -> when {
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
