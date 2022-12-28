@file:OptIn(DelicateCoroutinesApi::class)

package com.ygohel18

import com.ygohel18.data.user.MongoUserDataSource
import com.ygohel18.data.user.User
import com.ygohel18.plugins.*
import com.ygohel18.security.hashing.SHA256HashingService
import com.ygohel18.security.token.JwtTokenService
import com.ygohel18.security.token.TokenConfig
import io.ktor.server.application.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.KMongo
import org.litote.kmongo.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val mongoUser = System.getenv("MONGO_USER")
    val mongoPassword = System.getenv("MONGO_PASS")
    val mongoHost = System.getenv("MONGO_HOST")
    val mongoDb = System.getenv("MONGO_DB")

    val db = KMongo.createClient(
        connectionString = "mongodb+srv://$mongoUser:$mongoPassword@$mongoHost/$mongoDb?retryWrites=true&w=majority"
    ).getDatabase(mongoDb)

    val userDataSource = MongoUserDataSource(db)
//    GlobalScope.launch {
//        val user = User(
//            username = "test",
//            password = "test",
//            salt = "test",
//            email = "test@test.com",
//            subscription = "freemium"
//        )
//        userDataSource.insertUser(user)
//    }

    val tokenService = JwtTokenService()

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 90L * 1000L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )

    val hashingService = SHA256HashingService()

    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig)
}
