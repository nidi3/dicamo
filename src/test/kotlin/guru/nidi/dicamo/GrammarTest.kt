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
            parlem,     parlar/parler/parlemir
            dorme,      dormer/dormeir
            dormes,     dormar/dormer/dormetre/dormeir     
            dormert,    dormertre/dormertir
            dormerta,   dormertar/dormertair
            dormerts,   dormertre/dormertir
            dormertes,  dormertar/dormertre/dormertendre/dormerteir
            omple,      ompler/ompleir/omplir 
            omplit,     omplitre/omplir 
            omplert,    omplertre/omplertir/omplir 
            acomple,    acompler/acompleir
            acompleix,  acompleixer/acompleixir/acomplir
            acomplert,  acomplertre/acomplertir/acomplir
            obre,       obrer/obreir/obrir
            obrit,      obritre/obrir 
            obert,      obertre/obertir/obrir 
            cobre,      cobrer/cobreir
            cobreix,    cobrèixer/cobreixir/cobrir
            cobert,     cobertre/cobertir/cobrir
            tens,       tendre/tenir
            tingut,     tingutir/tenir
            mantinc,    mantincer/mantincir/mantenir
            vens,       vendre/venir
            vingut,     vingutir/venir
            previnc,    previncer/previncir/prevenir
            morta,      mortar/mortair/morir
            cullo,      cullar/culler/cullir/collir
            collim,     collimer/collir
            surto,      surtar/surtre/surtir/sortir
            sortim,     sortimer/sortir
            escupo,     escupar/escupre/escupir/escopir
            escopim,    escopimer/escopir
            esglais,    esglaiar/esglair      
            esglaiis,   esglaiar      
            canvis,     canvar/canver/canvir      
            canviis,    canviar      
            tus,        ture/tuir/tossir
            tossim,     tossimer/tossir
            cuso,       cusar/cuser/cusir/cosir
            coso,       cosar/coser/cosir
            lluu,       lluure/lluuir/lluir
            llueixo,    llueixar/llueixer/llueixir/lluir
            lluo,       lluar/llure/lluir
            lluisses,   lluissar/lluisser/lluisseir/lluir
            ixo,        ixar/ixer/ixir/eixir
            eixim,      eiximer/eixir
            reixo,      reixar/rèixer/reixir/reeixir
            desixo,     desixar/desixer/desixir/deseixir
            sobreixo,   sobreixar/sobrèixer/sobreixir/sobrir/sobreeixir
            sotaixo,    sotaixar/sotàixer/sotaixir/sotaeixir
            teixo,      teixar/teixer/teixir/tir
            put,        pre/putir/pudir
            impres,     imprar/imprer/imprendre/impreir/imprimir
            vaig,       anar/vaigir
            anem,       anar/anemir
            estic,      estar/esticer/esticir
            soc,        socer/ser/socir
            seria,      seriar/ser/serir
            fora,       forar/ser/forair
            faig,       fer/faigir
            refaig,     refer/refaigir
            puc,        pucer/poder/pucir
            saps,       saber/sapre/sabre/sapir
            haig,       haver/haigir
            veig,       veure/veigir
            preveig,    preveure/preveigir
            visc,       viscer/viure/viscir
            convisc,    conviscer/conviure/conviscir
            reps,       repre/rebre/repir
            perceps,    percepre/percebre/percepir
            capiga,     capigar/cabre/capigair
            apres,      aprar/aprer/aprendre/apreir
            aprenc,     aprèncer/aprendre/aprencir
            prengui,    prenguar/prengar/prengure/prendre/prenguir
            atenent,    atendre/atenentir
            venut,      vendre/venutir
            confos,     confondre/confoir
            resposta,   respostar/respondre/respostair
            promes,     promar/pròmer/prometre/promeir
            prometia,   prometiar/prometre/prometir
            hagut,      haver/heure/hagutir
            hec,        hecer/heure/hecir
            corres,     corrar/córrer/correndre/correir
            corregut,   córrer/corregutir
"""
    )
    fun testInfinitivesOf(word: String, expected: String?) {
        val expectedList = expected?.split("/") ?: listOf()
        assertEquals(expectedList, infinitivesOf(word))
    }
}
