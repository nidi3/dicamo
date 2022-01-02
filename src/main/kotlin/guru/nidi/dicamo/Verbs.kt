package guru.nidi.dicamo

import guru.nidi.dicamo.Form.*

//for verb conjugations, see
//https://ca.wiktionary.org/wiki/Viccionari:Conjugaci%C3%B3_en_catal%C3%A0/paradigmes

val verbs = mapOf(
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
            "/an",
            PRESENT("vaig", "vas", "va", null, null, "van"),
            FUTUR("anire", "aniras", "anira", "anirem", "anireu", "aniran"),
            SUB_PRESENT("vagi", "vagis", "vagi", null, null, "vagin"),
            CONDICIONAL("aniria", "aniries", "aniria", "aniriem", "anirieu", "anirien"),
            IMPERATIU("ves", "vagi", null, null, "vagin")
        ),
        group(
            "est",
            PRESENT("ic", "as", null, null, null, "an"),
            SIMPLE("igui", "igueres", "igue", "iguerem", "iguereu", "igueren"),
            SUB_PRESENT("igui", "iguis", "igui", "iguem", "igueu", "iguin"),
            SUB_IMPERFET("igues", "iguessis", "igues", "iguessim", "iguessiu", "iguessin"),
            IMPERATIU("igues", "igui", "iguem", "igueu", "iguin")
        ),
        group(
            "-a/-ai",
            SIMPLE("i"),
            SUB_PRESENT("i", "is", "i", null, null, "in"),
            IMPERATIU(null, "i", null, null, "ain")
        ),
    ),
    ending(
        "er",
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
        ),
        group(
            "/s",
            GERUNDI(null, "essent"),
            PARTICIPI("estat", "estada", "estats", "estades", "sigut", "siguda", "siguts", "sigudes"),
            PRESENT("soc", "ets", "es", "som", "sou", "son"),
            IMPERFET("era", "eres", "era", "erem", "ereu", "eren"),
            SIMPLE("fui", "fores", "fou", "forem", "foreu", "foren"),
            SUB_PRESENT("sigui", "siguis", "sigui", "siguem", "sigueu", "siguin"),
            SUB_IMPERFET("fos", "fossis", "fos", "fossim", "fossiu", "fossin"),
            CONDICIONAL(null, null, null, null, null, null, "fora", "fores", "fora", "forem", "foreu", "foren"),
            IMPERATIU("sigues", "sigui", "siguem", "sigueu", "siguin"),
        ),
        group(
            "-f",
            PARTICIPI("et", "eta", "ets", "etes"),
            PRESENT("aig", "as", "a", null, null, "an"),
            IMPERFET("eia", "eies", "eia", "eiem", "eieu", "eien"),
            SIMPLE("iu", null, "eu"),
            FUTUR("are", "aras", "ara", "arem", "areu", "aran"),
            SUB_PRESENT("aci", "acis", "aci", null, null, "acin"),
            CONDICIONAL("aria", "aries", "aria", "ariem", "arieu", "arien"),
            IMPERATIU("es", "aci", null, null, "acin"),
        ),
        group(
            "p/pod",
            PARTICIPI("ogut", "oguda", "oguts", "ogudes"),
            PRESENT("uc", "ots", "ot"),
            SIMPLE("ogui", "ogueres", "ogue", "oguerem", "oguereu", "ogueren"),
            FUTUR("odre", "odras", "odra", "odrem", "odreu", "odran"),
            SUB_PRESENT("ugui", "uguis", "ugui", "uguem", "ugueu", "uguin"),
            IMPERFET("ogues", "oguessis", "ogues", "oguessim", "oguessiu", "oguessin"),
            CONDICIONAL("odria", "odries", "odria", "odriem", "odrieu", "odrien"),
            IMPERATIU("ugues", "ugui", "uguem", "ugueu", "uguin"),
        ),
        group(
            "s/sab",
            PRESENT("e", "aps", "ap"),
            FUTUR("abre", "abras", "abra", "abrem", "abreu", "abran"),
            SUB_PRESENT("apiga", "apigues", "apiga", "apiguem", "apigueu", "apiguen"),
            CONDICIONAL("abria", "abries", "abria", "abriem", "abrieu", "abrien"),
            IMPERATIU("apigues", "apiga", "apiguem", "apigueu", "apiguen")
        ),
        group(
            "h/hav",
            PARTICIPI("agut", "aguda", "aguts", "agudes"),
            PRESENT("e", "as", "a", null, null, "an", "aig", "em", "eu"),
            SIMPLE("agui", "agueres", "ague", "aguerem", "aguereu", "agueren"),
            FUTUR("aure", "auras", "aura", "aurem", "aureu", "auran"),
            SUB_PRESENT("agi", "agis", "agi", "agim", "agiu", "agin", "aguem", "agueu"),
            SUB_IMPERFET(
                "agues", "aguessis", "agues", "aguessim", "aguessiu", "aguessin",
                "aguesses", "aguessem", "aguesseu", "aguessen"
            ),
            CONDICIONAL(
                "auria", "auries", "auria", "auriem", "aurieu", "aurien",
                "aguera", "agueras", "aguera", "aguerem", "aguereu", "agueren"
            )
        ),
        group(
            "-orr",
            PARTICIPI("egut", "eguda", "eguts", "egudes"),
            PRESENT(null, "es", "e"),
            SIMPLE("egui", "egueres", "egue", "eguerem", "eguereu", "egueren"),
            IMPERFET("egues", "eguessis", "egues", "eguessim", "eguessiu", "eguessin"),
            IMPERATIU("e")
        )
    ),
    ending(
        "re",
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
        ),
        group(
            "-v/-veu",
            GERUNDI("eient"),
            PARTICIPI("ist", "ista", "ists", "istes"),
            PRESENT("eig", null, null, "eiem", "eieu"),
            IMPERFET("eia", "eies", "eia", "eiem", "eieu", "eien"),
            SIMPLE(
                "iu", "eieres", "eie", "eierem", "eiereu", "eieren",
                "eres", "eu", "erem", "ereu", "eren"
            ),
            SUB_PRESENT("egi", "egis", "egi", "egem", "egeu", "egin"),
            SUB_IMPERFET("eies", "eiessis", "eies", "eiessim", "eiessiu", "eiessin"),
            IMPERATIU("eges", "egi", "egem", "egeu", "egin", "es", "eieu")
        ),
        group(
            "-vi/-viu",
            GERUNDI("vint"),
            PARTICIPI("scut", "scuda", "scuts", "scudes"),
            PRESENT("sc", null, null, "vim", "viu"),
            IMPERFET("via", "vies", "via", "viem", "vieu", "vien"),
            SIMPLE("squi", "squeres", "sque", "squerem", "squereu", "squeren"),
            SUB_PRESENT("squi", "squis", "squi", "squem", "squeu", "squin"),
            SUB_IMPERFET("sques", "squessis", "sques", "squessim", "squessiu", "squessin"),
            IMPERATIU(null, "squi", "squem", "viu", "squin")
        ),
        group(
            "-/-b",
            PRESENT(null, "ps", "p"),
            IMPERATIU("p")
        ),
        group(
            "ca/cab",
            PRESENT(null, "ps", "p"),
            SUB_PRESENT("piga", "pigues", "piga", "piguem", "pigueu", "piguen"),
            IMPERATIU("p", "piga", "piguem", null, "piguen")
        ),
        group(
            "-re/-rend,-te/-tend,-o/-ond",
            GERUNDI("nent"),
            PARTICIPI("s", "sa", "sos", "ses"),
            PRESENT("nc", "ns", "n", "nem", "neu", "nen"),
            IMPERFET("nia", "nies", "nia", "niem", "nieu", "nien"),
            SIMPLE("ngui", "ngueres", "ngue", "nguerem", "nguereu", "ngueren"),
            SUB_PRESENT("ngui", "nguis", "ngui", "nguem", "ngueu", "nguin"),
            SUB_IMPERFET("ngues", "nguessis", "ngues", "nguessim", "nguessiu", "nguessin"),
            IMPERATIU("n", "ngui", "nguem", "neu", "nguin")
        ),
        group(
            "-po/-pond",
            GERUNDI("nent"),
            PARTICIPI("st", "sta", "stos", "stes"),
            PRESENT("nc", "ns", "n", "nem", "neu", "nen"),
            IMPERFET("nia", "nies", "nia", "niem", "nieu", "nien"),
            SIMPLE("ngui", "ngueres", "ngue", "nguerem", "nguereu", "ngueren"),
            SUB_PRESENT("ngui", "nguis", "ngui", "nguem", "ngueu", "nguin"),
            SUB_IMPERFET("ngues", "nguessis", "ngues", "nguessim", "nguessiu", "nguessin"),
            IMPERATIU("n", "ngui", "nguem", "neu", "nguin")
        ),
        group(
            "-ven/-vend",
            GERUNDI("ent"),
            PARTICIPI("ut", "uda", "uts", "udes"),
            PRESENT("c", "s", "", "em", "eu", "en"),
            IMPERFET("ia", "ies", "ia", "iem", "ieu", "ien"),
            SIMPLE("gui", "gueres", "gue", "guerem", "guereu", "gueren"),
            SUB_PRESENT("gui", "guis", "gui", "guem", "gueu", "guin"),
            SUB_IMPERFET("gues", "guessis", "gues", "guessim", "guessiu", "guessin"),
            IMPERATIU("", "gui", "guem", "eu", "guin")
        ),
        group(
            "-me/-met",
            PARTICIPI("s", "sa", "sos", "ses")
        ),
        group(
            "h/heu",
            GERUNDI("avent"),
            PARTICIPI("agut", "aguda", "aguts", "agudes"),
            PRESENT("ec", null, null, "avem", "aveu"),
            IMPERFET("avia", "avias", "avia", "aviem", "avieu", "avien"),
            SIMPLE("agui", "agueres", "ague", "aguerem", "aguereu", "agueren"),
            FUTUR("aure", "auras", "aura", "aurem", "aureu", "auran"),
            SUB_PRESENT("egui", "eguis", "egui", "aguem", "agueu", "eguin"),
            SUB_IMPERFET("agues", "aguessis", "agues", "aguessim", "aguessiu", "aguessin"),
            CONDICIONAL("auria", "auries", "auria", "auriem", "aurieu", "aurien"),
            IMPERATIU(null, "egui", "aguem", "aveu", "eguin")
        ),
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

enum class Form {
    GERUNDI, PARTICIPI, PRESENT, IMPERFET, SIMPLE, FUTUR, SUB_PRESENT, SUB_IMPERFET, CONDICIONAL, IMPERATIU;

    operator fun invoke(vararg forms: String?) = this to listOf(*forms)
}

private fun ending(ending: String, vararg groups: Pair<String, Map<Form, List<String?>>>) =
    ending to mapOf(*groups)

private fun group(vararg forms: Pair<Form, List<String?>>) = group("", *forms)

private fun group(name: String = "", vararg forms: Pair<Form, List<String?>>) = name to mapOf(*forms)
