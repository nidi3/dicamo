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
                Verb(
                    parts[0],
                    parts[1],
                    if (parts.size <= 2 || parts[2] == "") UNKNOWN else valueOf(parts[2]),
                    if (parts.size <= 3 || parts[3] == "") -1 else Integer.parseInt(parts[3])
                )
            }
        }
    }
}

data class Verb(val name: String, val url: String, val type: Type, val frequency: Int) {
    fun antiPronominal() =
        if (name.endPronoun() != "") Verb(name.pronounLess(), url.pronounLess(), UNKNOWN, -1)
        else if (name.endsWith("re")) Verb(name + "'s", url + "'s", UNKNOWN, -1)
        else Verb(name + "-se", url + "-se", UNKNOWN, -1)
}


enum class Type {
    UNKNOWN, PUR, INCOATIU, BOTH, NONE;
}
