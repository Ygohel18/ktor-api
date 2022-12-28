package com.ygohel18.plugins

import com.ygohel18.*
import com.ygohel18.data.user.UserDataSource
import com.ygohel18.security.hashing.HashingService
import com.ygohel18.security.token.TokenConfig
import com.ygohel18.security.token.TokenService
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {

    routing {
        login(hashingService, userDataSource, tokenService, tokenConfig)
        register(hashingService, userDataSource)
        authenticate()
        getSecretInfo()
    }
}
