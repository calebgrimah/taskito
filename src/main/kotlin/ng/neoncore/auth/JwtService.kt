package ng.neoncore.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import ng.neoncore.user.model.User
import java.util.*

class JwtService {

    private val issuer = "taskitoServer"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val audience = "use audience"
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()
    fun generateToken(user: User): String =
        JWT.create().withAudience(audience).withSubject("Authentication").withIssuer(issuer)
            .withClaim("id", user.userId)
            .withExpiresAt(expiresAt()).sign(algorithm)

    private fun expiresAt(): Date =
        Date(System.currentTimeMillis() + 3_600_000) //valid for one hour

    fun hashFunction(s: String) = hash(s)

}