package ng.neoncore.user.model

import io.ktor.server.auth.*
import  java.io.Serializable

data class User(
    val userId: Int,
    val email: String,
    val username: String,
    val passwordHash: String,
) : Serializable, Principal