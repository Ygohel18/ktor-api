package com.ygohel18

import com.ygohel18.authenticate
import com.ygohel18.data.requests.AuthRequest
import com.ygohel18.data.responses.AuthResponse
import com.ygohel18.data.user.MongoUserDataSource
import com.ygohel18.data.user.User
import com.ygohel18.data.user.UserDataSource
import com.ygohel18.security.hashing.HashingService
import com.ygohel18.security.hashing.SHA256HashingService
import com.ygohel18.security.hashing.SaltedHash
import com.ygohel18.security.token.TokenClaim
import com.ygohel18.security.token.TokenConfig
import com.ygohel18.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.json.simple.JSONObject

fun Route.register(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("register") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areaFieldsBlank = request.username.isBlank() || request.password.isBlank() || request.email.isBlank()
        val isPwTooShort = request.password.length < 8

        if (areaFieldsBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt,
            email = request.email,
            subscription = request.subscription
        )

        val wasAcknowledged = userDataSource.insertUser(user)

        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.login(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("login") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "User not found")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incorrect password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(status = HttpStatusCode.OK, message = "Your userId = $userId")
        }
    }
}