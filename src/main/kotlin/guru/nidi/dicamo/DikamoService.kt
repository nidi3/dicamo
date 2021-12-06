package guru.nidi.dicamo

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.ISO_8859_1
import java.nio.charset.StandardCharsets.UTF_8
import java.util.regex.Pattern

private val WORD = Pattern.compile("GECART=([^&']+)")
private const val URL_BASE = "http://www.diccionari.cat"
private const val QUERY_URL = "/cgi-bin/AppDLC3.exe?APP=CERCADLC&GECART="
private const val CONJUG_URL = "/cgi-bin/AppDLC3.exe?APP=CONJUGA&GECART="
private const val ENTRY_URL = "/lexicx.jsp?GECART="

object DikamoService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun query(query: String): List<Entry> {
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

    fun fetchEntry(id: String) = formatDocument(fetch(ENTRY_URL, id), "entry").toString()

    fun fetchConjug(id: String) = formatDocument(fetch(CONJUG_URL, id), "conjug").toString()

    private fun formatDocument(document: Document, type: String) =
        document.select(".CentreTextTD table").apply {
            addClass(type)
            select(".enc").forEach {
                val word = it.textNodes().first().text().trim()
                for (lang in listOf("es", "en", "de", "fr", "it").reversed()) {
                    it.parent()!!.insertChildren(it.elementSiblingIndex() + 1, translateLink(document, word, lang))
                }
            }
            select("font").forEach { it.removeAttr("size") }
            select("br,img").forEach { it.remove() }
            select("a").forEach { a ->
                val href = a.attr("href")
                when {
                    href.startsWith(CONJUG_URL) ->
                        wordId(href)?.let {
                            a.attr("href", "/${"conjug"}/$it")
                        }
                    a.hasClass("verb_link") -> a.remove()
                }
            }
        }

    private fun translateLink(document: Document, word: String, lang: String) =
        document.createElement("a").apply {
            attr(
                "href",
                "https://translate.google.ch/?sl=ca&tl=$lang&text=" + encode(word)
            )
            text("$lang ")
        }

    private fun wordId(word: String): String? {
        val matcher = WORD.matcher(word)
        return if (!matcher.find()) null else matcher.group(1)
    }

    private fun fetch(path: String, query: String) =
        Jsoup.connect(URL_BASE + path + encode(query, ISO_8859_1)).get()

    private fun encode(s: String, charset: Charset = UTF_8) =
        URLEncoder.encode(s, charset)
}

class Entry(val id: String?, val word: String)
