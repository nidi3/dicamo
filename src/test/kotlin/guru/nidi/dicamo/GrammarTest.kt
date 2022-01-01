package guru.nidi.dicamo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GrammarTest {
    @ParameterizedTest
    @CsvSource(
        textBlock = """
            parlem,     em, ar, parlar
            trenques,   es, ar, trenquar/trencar 
            comences,   es, ar, començar 
            jugues,     es, ar, juguar/jugar
            viatges,    es, ar, viatjar         
            viatges,    es, er, viatger
            canvis,     is, ar, canvar                     
            canviis,    is, ar, canviar                     
            esglais,    is, ar, esglaiar                     
            esglaiis,   is, ar, esglaiiar                     
"""
    )
    fun replaceEnding(word: String, oldEnding: String, newEnding: String, expected: String) {
        val expectedList = expected.split("/")
        assertEquals(expectedList, word.replaceEnding(oldEnding, newEnding))
    }
}
