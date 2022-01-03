package guru.nidi.dicamo

import guru.nidi.dicamo.Type.UNKNOWN
import guru.nidi.dicamo.Type.valueOf
import java.io.File

object VerbList {
    val verbFile = File("src/main/resources/verbs.txt")
    val verbs = readVerbs().associateBy { it.name.pronounLess().normalize() }

    fun readVerbs(): List<Verb> {
        return verbFile.readLines().map { line ->
            line.split(Regex("""\s+""")).let { parts ->
                Verb(parts[0], parts[1], valueOf(parts[2]))
            }
        }
    }
}

data class Verb(val name: String, val url: String, val type: Type) {
    fun ending() = name.pronounLess().takeLast(2)

    fun antiPronominal() =
        if (name.endPronoun() != "") Verb(name.pronounLess(), url.pronounLess(), UNKNOWN)
        else if (name.endsWith("re")) Verb(name + "'s", url + "'s", UNKNOWN)
        else Verb(name + "-se", url + "-se", UNKNOWN)
}


enum class Type {
    UNKNOWN, PUR, INCOATIU, BOTH, NONE;
}
