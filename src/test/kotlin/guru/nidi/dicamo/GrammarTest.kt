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
            parlem,     parlar/parler/parlre/parlemir
            dorme,      dormer/dormre/dormeir
            dormes,     dormar/dormer/dormre/dormetre/dormeir     
            dormert,    dormerter/dormertre/dormertir
            dormerta,   dormertar/dormertaer/dormertare/dormertair
            dormerts,   dormerter/dormertre/dormertir
            dormertes,  dormertar/dormerter/dormertre/dormertendre/dormerteir
            omple,      ompler/omplre/ompleir/omplir 
            omplit,     ompliter/omplitre/omplir 
            omplert,    omplerter/omplertre/omplertir/omplir 
            acomple,    acompler/acomplre/acompleir
            acompleix,  acompleixer/acompleixre/acompleixir/acomplir
            acomplert,  acomplerter/acomplertre/acomplertir/acomplir
            obre,       obrer/obrre/obreir/obrir
            obrit,      obriter/obritre/obrir 
            obert,      oberter/obertre/obertir/obrir 
            cobre,      cobrer/cobrre/cobreir
            cobreix,    cobreixer/cobreixre/cobreixir/cobrir
            cobert,     coberter/cobertre/cobertir/cobrir
            tens,       tener/tenre/tendre/tenir
            tingut,     tinger/tingre/tingutir/tenir
            mantinc,    mantincer/mantincre/mantincir/mantenir
            vens,       vener/venre/vendre/venir
            vingut,     vinger/vingre/vingutir/venir
            previnc,    previncer/previncre/previncir/prevenir
            morta,      mortar/mortaer/mortare/mortair/morir
            cullo,      cullar/culler/cullre/cullir/collir
            collim,     collimer/collimre/collir
            surto,      surtar/surter/surtre/surtir/sortir
            sortim,     sortimer/sortimre/sortir
            escupo,     escupar/escuper/escupre/escupir/escopir
            escopim,    escopimer/escopimre/escopir
            esglais,    esglaar/esglaiar/esglaer/esglare/esglair      
            esglaiis,   esglaiar/esglaier/esglaire/esglaiir      
            canvis,     canvar/canver/canvre/canvir      
            canviis,    canviar/canvier/canvire/canviir      
            tus,        tuer/ture/tuir/tossir
            tossim,     tossimer/tossimre/tossir
            cuso,       cusar/cuser/cusre/cusir/cosir
            coso,       cosar/coser/cosre/cosir
            lluu,       lluuer/lluure/lluuir/lluir
            llueixo,    llueixar/llueixer/llueixre/llueixir/lluir
            lluo,       lluar/lluer/llure/lluir
            lluisses,   lluissar/lluisser/lluissre/lluisseir/lluir
            ixo,        ixar/ixer/ixre/ixir/eixir
            eixim,      eiximer/eiximre/eixir
            reixo,      reixar/reixer/reixre/reixir/rir/reeixir
            desixo,     desixar/desixer/desixre/desixir/deseixir
            sobreixo,   sobreixar/sobreixer/sobreixre/sobreixir/sobrir/sobreeixir
            sotaixo,    sotaixar/sotaixer/sotaixre/sotaixir/sotaeixir
            teixo,      teixar/teixer/teixre/teixir/tir
            put,        per/pre/putir/pudir
            impres,     imprar/imprer/imprre/imprendre/impreir/imprimir
            vaig,       anar/vaiger/vaigre/vaigir
            anem,       anar/aner/anre/anemir
            estic,      estar/esticer/esticre/esticir
            soc,        socer/ser/socre/socir
            seria,      seriar/ser/sre/serir
            fora,       forar/foraer/ser/forare/forair
            faig,       faiger/fer/faigre/faigir
            refaig,     refaiger/refer/refaigre/refaigir
            puc,        pucer/poder/pucre/pucir
            saps,       saper/saber/sapre/sabre/sapir
            haig,       haiger/haver/haigre/haigir
            veig,       veiger/veigre/veure/veigir
            preveig,    preveiger/preveigre/preveure/preveigir
            visc,       viscer/viscre/viure/viscir
            convisc,    conviscer/conviscre/conviure/conviscir
            reps,       reper/repre/rebre/repir
            perceps,    perceper/percepre/percebre/percepir
            capiga,     capigar/capigaer/capigare/cabre/capigair
            apres,      aprar/aprer/aprre/aprendre/apreir
            aprenc,     aprencer/aprencre/aprendre/aprencir
            prengui,    prenguar/prengar/prenguer/prengure/prendre/prenguir
            atenent,    atener/atenre/atendre/atenentir
            venut,      vener/venre/vendre/venutir
            confos,     confoer/confore/confondre/confoir
            resposta,   respostar/respostaer/respostare/respondre/respostair
            promes,     promar/promer/promre/prometre/promeir
            prometia,   prometiar/prometer/prometre/prometir
            hagut,      hager/haver/hagre/heure/hagutir
            hec,        hecer/hecre/heure/hecir
            corres,     corrar/correr/corrre/correndre/correir
"""
    )
    fun testInfinitivesOf(word: String, expected: String?) {
        val expectedList = expected?.split("/") ?: listOf()
        assertEquals(expectedList, infinitivesOf(word))
    }
}
