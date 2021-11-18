package guru.nidi.dicamo

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.ISO_8859_1
import java.util.regex.Pattern

private val WORD = Pattern.compile("GECART=([^&']+)")
private const val URL_BASE = "http://www.diccionari.cat"
private const val QUERY_URL = "/cgi-bin/AppDLC3.exe?APP=CERCADLC&GECART="
private const val CONJUG_URL = "/cgi-bin/AppDLC3.exe?APP=CONJUGA&GECART="
private const val WORD_URL = "/lexicx.jsp?GECART="

@Controller
class DicamoController {
    val log = LoggerFactory.getLogger(javaClass)

    @GetMapping(value = ["/query/{query}"], produces = ["application/json"])
    @ResponseBody
    fun query(@PathVariable query: String): List<Entry> {
        try {
            val document = fetch(QUERY_URL, query)
            if (document.head().childNodeSize() == 1) {
                val script = document.head().childNode(0).childNode(0).toString()
                return wordId(script)?.let { listOf(Entry(it, query)) } ?: listOf()
            }
            val words = document.select(".CentreTextTD table")[0].select("a")
            return words.map { Entry(wordId(it.attr("href")), it.text()) }
        } catch (e: Exception) {
            log.error("Error querying {}", query, e)
            return listOf(Entry(null, e.message ?: "Unknown error"))
        }
    }

    @GetMapping("/")
    fun mask(): String {
        return "search.html"
    }

    @GetMapping("/entry/{id}")
    @ResponseBody
    fun word(@PathVariable id: String): ModelAndView {
        val content = formatDocument(fetch(WORD_URL, id), "entry")
        return ModelAndView("base", mapOf("body" to content.toString()))
    }

    @GetMapping("/conjug/{id}")
    @ResponseBody
    fun conjug(@PathVariable id: String): ModelAndView {
        val content = formatDocument(fetch(CONJUG_URL, id), "conjug")
        val formatted = content.toString()
            .replace("SUBJUNTIU", "SUBJ")
            .replace("CONDICIONAL", "COND")
            .replace("PRESENT", "PRES")
            .replace("IMPERFET", "IMP")
        return ModelAndView("base", mapOf("body" to formatted))
    }

    fun formatDocument(document: Document, type: String) =
        document.select(".CentreTextTD table").apply {
            addClass(type)
            select("font").forEach { it.removeAttr("size") }
            select("br,img").forEach { it.remove() }
            select("a").forEach { a ->
                val href = a.attr("href")
                when {
                    href.startsWith(CONJUG_URL) ->
                        wordId(href)?.let {
                            a.attr("href", wordUrl("conjug", it))
                        }
                    a.hasClass("verb_link") -> a.remove()
                }
            }
        }

    fun wordUrl(base: String, id: String) =
        ServletUriComponentsBuilder.fromCurrentContextPath().path("$base/").path(id).toUriString()

    fun wordId(word: String): String? {
        val matcher = WORD.matcher(word)
        return if (!matcher.find()) null else matcher.group(1)
    }

    fun fetch(path: String, query: String) = Jsoup.connect(
        URL_BASE + path + URLEncoder.encode(
            query,
            ISO_8859_1
        )
    ).get()
}

class Entry(val id: String?, val word: String)
