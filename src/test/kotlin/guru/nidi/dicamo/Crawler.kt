package guru.nidi.dicamo

import guru.nidi.dicamo.Type.*
import guru.nidi.dicamo.VerbList.readVerbs
import guru.nidi.dicamo.VerbList.verbFile
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Evaluator
import java.net.URL

object Crawler {
    val baseUrl = "https://ca.wiktionary.org"

    @JvmStatic
    fun main(args: Array<String>) {
        val verbs = fetchIrVerbs(readVerbs())
        writeVerbs(verbs)
    }

    fun fetchVerbs(): List<Verb> {
        val document =
            Jsoup.parse(URL("$baseUrl/wiki/Viccionari:Llista_de_verbs_en_català"), 5000)
        return document.getElementsByTag("a")
            .map { a -> Verb(a.text(), a.attr("href"), UNKNOWN) }
            .filter { verb -> verb.urlValid() }
    }

    fun fetchIrVerbs(verbs: List<Verb>): List<Verb> {
        val fetched = verbs.map { verb ->
            when {
                verb.type != UNKNOWN -> verb
                verb.ending() != "ir" -> verb.copy(type = NONE)
                else -> {
                    val document = Jsoup.parse(URL(baseUrl + verb.url), 5000)
                    val type = parseType(document)
                    println("${verb.name}: $type")
                    verb.copy(type = type)
                }
            }
        }
        return fetched.map { verb ->
            if (verb.type != UNKNOWN) verb
            else {
                try {
                    val document = Jsoup.parse(URL(baseUrl + verb.antiPronominal().url), 5000)
                    val type = parseType(document)
                    println("anti-pronominal ${verb.name}: $type")
                    verb.copy(type = type)
                } catch (e: HttpStatusException) {
                    verb
                }
            }
        }
    }

    fun parseType(document: Document): Type {
        fun parseString(s: String): Type {
            val incoatiu = "incoativa" in s
            val pur = "pura" in s
            return if (incoatiu && pur) BOTH else if (incoatiu) INCOATIU else if (pur) PUR else UNKNOWN
        }

        val tercera = document.select(Evaluator.Matches(Regex("Tercera conjugació").toPattern()))
            .filter { "NavHead" in it.classNames() }
            .map { parseString(it.text()) }
            .filter { it != UNKNOWN }
            .toSet()

        if (tercera.isEmpty()) {
            //dir i derivats
            val segona = document.select(Evaluator.Matches(Regex("Segona conjugació").toPattern()))
            if (segona.isNotEmpty()) return PUR

            val irregular = document.select(Evaluator.Matches(Regex("irregular").toPattern()))
            if (irregular.isNotEmpty()) return PUR
        }

        return if (tercera.size == 1) tercera.first() else UNKNOWN
    }

    fun writeVerbs(verbs: List<Verb>) {
        verbFile.printWriter().use { out ->
            verbs.forEach {
                out.print(it.name.padEnd(30))
                out.print(it.url.padEnd(30))
                out.print(it.type.name.padEnd(10))
                out.println()
            }
        }
    }
}

fun Verb.urlValid() = url.startsWith("/wiki") &&
        !url.startsWith("/wiki/Viccionari") && !url.startsWith("/wiki/Categoria") && !url.startsWith("/wiki/Especial")


