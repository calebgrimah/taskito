package ng.neoncore.user.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import ng.neoncore.API_VERSION
import ng.neoncore.auth.JwtService
import ng.neoncore.auth.MySession
import ng.neoncore.user.repository.UserRepository
import ng.neoncore.util.inject

const val USERS = "$API_VERSION/users"
const val USERS_LOGIN = "$USERS/login"
const val USERS_CREATE = "$USERS/create"


@KtorExperimentalLocationsAPI
@Location(USERS_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USERS_CREATE)
class CreateUserRoute


@KtorExperimentalLocationsAPI
fun Route.configureUsers() {
    val userRepository: UserRepository by inject()
    val jwtService: JwtService by inject()
    post<CreateUserRoute> {
        val signupParameters = call.receive<Parameters>()
        val password =
            signupParameters["password"] ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields pass")
        println("password -> : $password")
        val username =
            signupParameters["username"] ?: return@post call.respond(
                HttpStatusCode.Unauthorized,
                "Missing Fields username"
            )
        println("username -> : $username")
        val email =
            signupParameters["email"] ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields email")
        println("email -> : $email")
        val hash = jwtService.hashFunction(password)
        try {
            val newUser = userRepository.addUser(email, username, hash)
            newUser?.userId?.let {
                call.sessions.set(MySession(it))
                call.respondText(
                    jwtService.generateToken(newUser), status = HttpStatusCode.Created
                )
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Unable to create a user")
        }
    }

    post<UserLoginRoute> {
        val signInParameters = call.receive<Parameters>()
        val password =
            signInParameters["password"] ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        val email = signInParameters["email"] ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        val hashedPassword = jwtService.hashFunction(password)
        try {
            val currentUser = userRepository.findUserByEmail(email)
            currentUser?.userId?.let {
                if (currentUser.passwordHash == hashedPassword) {
                    call.sessions.set(MySession(it))
                    print("my current session${call.sessions.get<MySession>()?.userId}")
                    call.respondText(jwtService.generateToken(currentUser))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Failed to retrieve User")
                }
            }
        } catch (e: Throwable) {
            application.log.error("Failed to login user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems Retrieving User")
        }
    }

}