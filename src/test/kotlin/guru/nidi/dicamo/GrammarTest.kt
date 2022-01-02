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
            dormert,    dormerter/dormertre/dormertir
            dormerta,   dormertar/dormertaer/dormertair
            dormerts,   dormerter/dormertre/dormertir
            dormertes,  dormertar/dormerter/dormertre/dormertendre/dormerteir
            omple,      ompler/ompleir/omplir 
            omplit,     ompliter/omplitre/omplir 
            omplert,    omplerter/omplertre/omplertir/omplir 
            acomple,    acompler/acompleir
            acompleix,  acompleixer/acompleixir/acomplir
            acomplert,  acomplerter/acomplertre/acomplertir/acomplir
            obre,       obrer/obreir/obrir
            obrit,      obriter/obritre/obrir 
            obert,      oberter/obertre/obertir/obrir 
            cobre,      cobrer/cobreir
            cobreix,    cobrèixer/cobreixir/cobrir
            cobert,     coberter/cobertre/cobertir/cobrir
            tens,       tener/tendre/tenir
            tingut,     tinger/tingutir/tenir
            mantinc,    mantincer/mantincir/mantenir
            vens,       vener/vendre/venir
            vingut,     vinger/vingutir/venir
            previnc,    previncer/previncir/prevenir
            morta,      mortar/mortaer/mortair/morir
            cullo,      cullar/culler/cullir/collir
            collim,     collimer/collir
            surto,      surtar/surter/surtre/surtir/sortir
            sortim,     sortimer/sortir
            escupo,     escupar/escuper/escupre/escupir/escopir
            escopim,    escopimer/escopir
            esglais,    esglaar/esglaiar/esglaer/esglair      
            esglaiis,   esglaiar/esglaier/esglaiir      
            canvis,     canvar/canver/canvir      
            canviis,    canviar/canvier/canviir      
            tus,        tuer/ture/tuir/tossir
            tossim,     tossimer/tossir
            cuso,       cusar/cuser/cusir/cosir
            coso,       cosar/coser/cosir
            lluu,       lluuer/lluure/lluuir/lluir
            llueixo,    llueixar/llueixer/llueixir/lluir
            lluo,       lluar/lluer/llure/lluir
            lluisses,   lluissar/lluisser/lluisseir/lluir
            ixo,        ixar/ixer/ixir/eixir
            eixim,      eiximer/eixir
            reixo,      reixar/rèixer/reixir/rir/reeixir
            desixo,     desixar/desixer/desixir/deseixir
            sobreixo,   sobreixar/sobrèixer/sobreixir/sobrir/sobreeixir
            sotaixo,    sotaixar/sotàixer/sotaixir/sotaeixir
            teixo,      teixar/teixer/teixir/tir
            put,        per/pre/putir/pudir
            impres,     imprar/imprer/imprendre/impreir/imprimir
            vaig,       anar/vaiger/vaigir
            anem,       anar/aner/anemir
            estic,      estar/esticer/esticir
            soc,        socer/ser/socir
            seria,      seriar/ser/serir
            fora,       forar/foraer/ser/forair
            faig,       faiger/fer/faigir
            refaig,     refaiger/refer/refaigir
            puc,        pucer/poder/pucir
            saps,       saper/saber/sapre/sabre/sapir
            haig,       haiger/haver/haigir
            veig,       veiger/veure/veigir
            preveig,    preveiger/preveure/preveigir
            visc,       viscer/viure/viscir
            convisc,    conviscer/conviure/conviscir
            reps,       reper/repre/rebre/repir
            perceps,    perceper/percepre/percebre/percepir
            capiga,     capigar/capigaer/cabre/capigair
            apres,      aprar/aprer/aprendre/apreir
            aprenc,     aprèncer/aprendre/aprencir
            prengui,    prenguar/prengar/prenguer/prengure/prendre/prenguir
            atenent,    atener/atendre/atenentir
            venut,      vener/vendre/venutir
            confos,     confoer/confondre/confoir
            resposta,   respostar/respostaer/respondre/respostair
            promes,     promar/pròmer/prometre/promeir
            prometia,   prometiar/prometer/prometre/prometir
            hagut,      hager/haver/heure/hagutir
            hec,        hecer/heure/hecir
            corres,     corrar/córrer/correndre/correir
            corregut,   correger/córrer/corregutir
"""
    )
    fun testInfinitivesOf(word: String, expected: String?) {
        val expectedList = expected?.split("/") ?: listOf()
        assertEquals(expectedList, infinitivesOf(word))
    }
}
