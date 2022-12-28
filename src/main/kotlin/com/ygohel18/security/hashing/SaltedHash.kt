package com.ygohel18.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)
