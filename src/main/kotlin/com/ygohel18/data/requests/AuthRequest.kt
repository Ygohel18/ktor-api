package com.ygohel18.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val username: String,
    val password: String,
    val email: String = "",
    val subscription: String = "freemium"
)
