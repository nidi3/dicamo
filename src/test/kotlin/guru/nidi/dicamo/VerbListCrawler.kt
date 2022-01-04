package guru.nidi.dicamo

import guru.nidi.dicamo.Type.*
import guru.nidi.dicamo.VerbList.readVerbs
import guru.nidi.dicamo.VerbList.verbFile
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Evaluator
import java.net.URL

object VerbListCrawler {
    val baseUrl = "https://ca.wiktionary.org"

    @JvmStatic
    fun main(args: Array<String>) {
        writeVerbs(readVerbs().map { verb ->
            if (verb.frequency >= 0) verb
            else verb.copy(frequency = fetchFrequency(verb.name.pronounLess()) ?: -1)
        })
//        writeVerbs(readVerbs().map { verb ->
//            if (verb.type != UNKNOWN) verb
//            else verb.copy(type = fetchType(verb))
//        })
    }

    fun fetchVerbs(): List<Verb> {
        val document =
            Jsoup.parse(URL("$baseUrl/wiki/Viccionari:Llista_de_verbs_en_català"), 5000)
        return document.getElementsByTag("a")
            .map { a -> Verb(a.text(), a.attr("href"), UNKNOWN, -1) }
            .filter { verb -> verb.urlValid() }
    }

    fun fetchType(verb: Verb): Type =
        if (verb.name.ending() != "ir") NONE
        else {
            val document = Jsoup.parse(URL(baseUrl + verb.url), 5000)
            val type = parseType(document)
            println("${verb.name}: $type")
            if (type != UNKNOWN) type
            else try {
                val antiDocument = Jsoup.parse(URL(baseUrl + verb.antiPronominal().url), 5000)
                val antiType = parseType(antiDocument)
                println("anti-pronominal ${verb.name}: $antiType")
                antiType
            } catch (e: HttpStatusException) {
                type
            }
        }


    fun parseType(document: Document): Type {
        fun parseString(s: String): Type {
            val incoatiu = "incoativa" in s
            val pur = "pura" in s
            return if (incoatiu && pur) BOTH else if (incoatiu) INCOATIU else if (pur) PUR else UNKNOWN
        }

        val tercera = document.select(Evaluator.MatchesOwn(Regex("Tercera conjugació").toPattern()))
            .map { parseString(it.text()) }
            .filter { it != UNKNOWN }
            .toSet()

        if (tercera.isEmpty()) {
            //dir i derivats
            val segona = document.select(Evaluator.MatchesOwn(Regex("Segona conjugació").toPattern()))
            if (segona.isNotEmpty()) return PUR

            val irregular = document.select(Evaluator.MatchesOwn(Regex("irregular").toPattern()))
            if (irregular.isNotEmpty()) return PUR
        }

        return if (tercera.size == 1) tercera.first() else UNKNOWN
    }

    fun fetchFrequency(word: String): Int? = try {
        val document =
            Jsoup.connect("https://ctilc.iec.cat/scripts/CTILCQConc_Lemes2.asp")
                .data(
                    mapOf(
                        "cadlema" to word,
                        "cadlemacond" to "0",
                        "cg" to "",
                        "seccio" to "CTILC1",
                        "mostrat" to "1"
                    )
                )
                .post()
        val freq = document.select(Evaluator.MatchesOwn(Regex("Freqüència total").toPattern()))
        freq.firstOrNull()?.parent()?.text()?.let { text ->
            Regex("""\d+""").find(text)?.groupValues?.firstOrNull()?.let { count ->
                println("$word: $count")
                Integer.parseInt(count)
            }
        }
    } catch (e: HttpStatusException) {
        null
    }

    fun writeVerbs(verbs: List<Verb>) {
        verbFile.printWriter().use { out ->
            verbs.forEach {
                out.print(it.name.padEnd(30))
                out.print(it.url.padEnd(30))
                out.print(it.type.name.padEnd(10))
                out.print(it.frequency.toString().padStart(10))
                out.println()
            }
        }
    }
}

fun Verb.urlValid() = url.startsWith("/wiki") &&
        !url.startsWith("/wiki/Viccionari") && !url.startsWith("/wiki/Categoria") && !url.startsWith("/wiki/Especial")


