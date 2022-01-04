package guru.nidi.dicamo

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import java.net.URLDecoder
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

    fun query(query: String): List<List<Entry>> = try {
        val document = fetch(QUERY_URL, query)
        if (document.head().childNodeSize() == 1) {
            val script = document.head().childNode(0).childNode(0).toString()
            listOf(wordId(script)?.let { listOf(Entry("/entry/$it", query)) } ?: listOf())
        } else {
            val words = document.select(".CentreTextTD table")[0].select("a")
            words.map { listOf(Entry("/entry/" + wordId(it.attr("href")), it.text())) }
        }
    } catch (e: Exception) {
        log.error("Error querying {}", query, e)
        listOf(listOf(Entry(null, e.message ?: "Unknown error")))
    }

    fun extendedQuery(query: String): List<List<Entry>> =
        query.normalize().let { querySingular(it) + queryVerb(it) }

    private fun querySingular(query: String): List<List<Entry>> =
        baseNounsOf(query).mapNotNull { base ->
            val results = query(base)
            val entry =
                if (results.size == 1) results[0][0]
                else results.find { it[0].word.normalize() == base }?.get(0)
            entry?.let {
                listOf(
                    Entry(entry.link, query),
                    Entry(entry.link, "(${entry.word})")
                )
            }
        }

    private fun queryVerb(query: String): List<List<Entry>> = try {
        infinitivesOf(query).let { (dict, guess) ->
            dict
                .ifEmpty { guess }
                .filter { inf -> fetch(CONJUG_URL, inf).title() == "Conjugació" }
                .map { inf ->
                    val base = inf.pronounLess()
                    listOf(
                        Entry("/conjug/$inf#${query.normalize().pronounLess()}", query),
                        Entry("/?q=$base&go", "($base)")
                    )
                }
        }
    } catch (e: Exception) {
        log.error("Error querying verb {}", query, e)
        listOf(listOf(Entry(null, e.message ?: "Unknown error")))
    }

    fun fetchEntry(id: String) = formatDocument(fetch(ENTRY_URL, id), "entry").toString()

    fun fetchConjug(id: String) = formatDocument(fetch(CONJUG_URL, id), "conjug").toString()

    private fun formatDocument(document: Document, type: String) =
        document.select(".CentreTextTD table").apply {
            addClass(type)
            select(".enc").forEach {
                val word = it.textNodes().first().text().trim()
                addAfter(it, wikiLink(document, word))
                for (lang in listOf("es", "en", "de", "fr", "it").reversed()) {
                    addAfter(it, translateLink(document, word, lang))
                }
            }
            select(".verb").forEach {
                val word = it.textNodes().first().text().trim()
                addAfter(it, entryLink(document, word.pronounLess()))
            }
            select("font").forEach { it.removeAttr("size") }
            select("br,img").forEach { it.remove() }
            select("a").forEach { a ->
                val href = a.attr("href")
                when {
                    href.startsWith(CONJUG_URL) -> wordId(href)?.let {
                        a.attr("href", "/conjug/${decode(it)}")
                    }
                    a.hasClass("verb_link") -> a.remove()
                }
            }
            select(".VFORMA").forEach { span -> span.attr("id", span.text().normalize()) }
        }

    private fun addAfter(e: Element, new: Element) = e.parent()!!.insertChildren(e.elementSiblingIndex() + 1, new)

    private fun translateLink(document: Document, word: String, lang: String) =
        link(document, "https://translate.google.ch/?sl=ca&tl=$lang&text=" + encode(word), "$lang ")

    private fun entryLink(document: Document, word: String) =
        link(document, "/?q=$word&go", "definiciò")

    private fun wikiLink(document: Document, word: String) =
        link(document, "https://ca.wikipedia.org/wiki/$word", "viquipèdia ")

    private fun link(document: Document, href: String, text: String) =
        document.createElement("a").apply {
            attr("href", href)
            text(text)
        }

    private fun wordId(word: String): String? =
        WORD.matcher(word).let { matcher ->
            if (!matcher.find()) null else matcher.group(1)
        }

    private fun fetch(path: String, query: String) = Jsoup.connect(url(path, query)).get()

    private fun url(path: String, query: String) = URL_BASE + path + encode(query, ISO_8859_1)

    private fun encode(s: String, charset: Charset = UTF_8) = URLEncoder.encode(s, charset)

    private fun decode(s: String, charset: Charset = ISO_8859_1) = URLDecoder.decode(s, charset)
}

class Entry(val link: String?, val word: String)
