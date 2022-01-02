package guru.nidi.dicamo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
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
            esgla,      is,     ar, esglaiar                     
            esglai,     is,     ar, esglaiiar
            dorm,       im,     ir, dormir
            fuj,        o,      ir, fugir   
            cull,       o,      ir, collir   
            cull,       i,      ir, cullir/collir   
            surt,       o,      ir, sortir   
            surt,       i,      ir, surtir/sortir   
            escup,      o,      ir, escopir   
            escup,      i,      ir, escupir/escopir
"""
    )
    fun replaceEnding(base: String, oldEnding: String, newEnding: String, expected: String?) {
        val expectedList = expected?.split("/") ?: listOf()
        assertEquals(expectedList, base.replaceEnding(oldEnding, newEnding))
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
            parlem,     parlar/parler/parlre/parlemir
            dorme,      dormer/dormre/dormeir
            dormes,     dormar/dormer/dormre/dormeir     
            dormert,    dormerter/dormertre/dormertir
            dormerta,   dormertar/dormertaer/dormertare/dormertair
            dormerts,   dormerter/dormertre/dormertir
            dormertes,  dormertar/dormerter/dormertre/dormerteir
            omple,      ompler/omplre/ompleir/omplir 
            omplert,    omplerter/omplertre/omplertir/omplir 
            acomple,    acompler/acomplre/acompleir
            acompleix,  acompleixer/acompleixre/acompleixir/acomplir
            acomplert,  acomplerter/acomplertre/acomplertir/acomplir
"""
    )
    fun testInfinitivesOf(word: String, expected: String?) {
        val expectedList = expected?.split("/") ?: listOf()
        assertEquals(expectedList, infinitivesOf(word))
    }
}
