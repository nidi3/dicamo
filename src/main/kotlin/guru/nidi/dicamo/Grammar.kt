package guru.nidi.dicamo

import java.text.Normalizer

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

fun infinitivesOf(word: String): List<String> {
    val normalized = word.normalize()
    return VERB_ENDINGS.flatMap { (infs, endings) ->
        endings
            .filter { ending -> normalized.endsWith(ending) }
            .maxByOrNull { it.length }
            ?.let { longestEnding -> infs.map { inf -> normalized.dropLast(longestEnding.length) + inf } }
            ?: listOf()
    }
}

private fun String.normalize() =
    Regex("\\p{InCombiningDiacriticalMarks}+").replace(Normalizer.normalize(this, Normalizer.Form.NFKD), "")
