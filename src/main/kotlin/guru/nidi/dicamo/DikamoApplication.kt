package guru.nidi.dicamo

import com.github.mustachejava.DefaultMustacheFactory
import guru.nidi.dicamo.DikamoService.fetchConjug
import guru.nidi.dicamo.DikamoService.fetchEntry
import guru.nidi.dicamo.DikamoService.query
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.mustache.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        jackson()
    }
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }

    routing {
        resources("static")
        defaultResource("static/search.html")

        get("/query/{query}") {
            call.respond(query(call.parameters["query"]!!))
        }

        get("/entry/{id}") {
            val content = fetchEntry(call.parameters["id"]!!)
            call.respond(MustacheContent("base.html", mapOf("body" to content)))
        }

        get("/conjug/{id}") {
            val content = fetchConjug(call.parameters["id"]!!)
                .replace("SUBJUNTIU", "SUBJ")
                .replace("CONDICIONAL", "COND")
                .replace("PRESENT", "PRES")
                .replace("IMPERFET", "IMP")
            call.respond(MustacheContent("base.html", mapOf("body" to content)))
        }
    }
}
