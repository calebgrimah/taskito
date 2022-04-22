package ng.neoncore.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import ng.neoncore.auth.JwtService
import ng.neoncore.user.repository.UserRepository
import ng.neoncore.util.inject

fun Application.configureSecurity() {
    val jwtService: JwtService by inject()
    val repository: UserRepository by inject()
    authentication {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Taskito Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = repository.findUser(claimString)
                user
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
            println("----")
            println("hashed password : -> :${jwtService.hashFunction("ds")}")
            println("----")
        }
    }
}
