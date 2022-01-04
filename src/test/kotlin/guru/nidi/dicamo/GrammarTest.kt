package guru.nidi.dicamo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.CsvSource

class GrammarTest {
    @ParameterizedTest
    @CsvSource(
        textBlock = """
            parl,       em,     ar, parlar
            trenqu,     es,     ar, trenquar/trencar 
            comenc,     es,     ar, començar 
            jugu,       es,     ar, juguar/jugar
            viatg,      es,     ar, viatjar         
            viatg,      es,     er, viatger
            canv,       is,     ar, canvar                     
            canvi,      is,     ar, canviar                     
            dorm,       im,     ir, dormir
            fuj,        o,      ir, fugir   
            venç,       ,       er, vencer   
"""
    )
    fun replaceEnding(base: String, oldEnding: String?, newEnding: String, expected: String?) {
        val expectedList = expected?.split("/") ?: listOf()
        assertEquals(expectedList, base.replaceEnding(oldEnding ?: "", newEnding))
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
            avis,       avi
            pares,      pare/para
            fargues,    fargue/fargua/farga
            oques,      oque/oqua/oca
            llengues,   llengue/llengua/llenga
            pasques,    pasque/pasqua/pasca
            places,     place/plaça
            pluges,     pluge/pluja
            platges,    platge/platja
            pans,       pan/pa
            gasos,      gaso/gas
            gossos,     gosso/goss/gos
            calaixos,   calaixo/calaix
            fajos,      fajo/faig
            mitjos,     mitjo/mitig/mig
            boscos,     bosco/bosc
"""
    )
    fun singularNounsOf(plural: String, singulars: String) {
        val expectedList = singulars.split("/")
        assertEquals(expectedList, singularNounsOf(plural))
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
            noia,           noi
            neboda,         nebod/nebot
            lloba,          llob/llop
            serva,          serv/serf
            gossa,          goss/gos
            germana,        german/germa
            sacerdotessa,   sacerdotess/sacerdotes/sacerdot
"""
    )
    fun masculinNounsOf(feminin: String, masculins: String) {
        val expectedList = masculins.split("/")
        assertEquals(expectedList, masculinNounsOf(feminin))
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["verbs.csv"])
    fun testInfinitivesOf(word: String, dict: String?, possible: String?) {
        val dictList = dict?.split("/") ?: listOf()
        val possibleList = possible?.split("/") ?: listOf()
        assertEquals(Pair(dictList, possibleList), infinitivesOf(word))
    }
}
