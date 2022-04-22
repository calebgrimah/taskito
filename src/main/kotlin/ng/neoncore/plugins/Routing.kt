package ng.neoncore.plugins

import io.ktor.server.application.*
import io.ktor.server.locations.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import ng.neoncore.auth.MySession
import ng.neoncore.task.routes.configureTaskRequests
import ng.neoncore.user.routes.configureUsers
import java.io.File

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.configureRouting() {
    install(Locations)
    install(Sessions) {
        header<MySession>("user_session", directorySessionStorage(File("build/.sessions")))
    }
    routing {
        trace { application.log.trace(it.buildText()) }
        configureUsers()
        configureTaskRequests()
    }
}
