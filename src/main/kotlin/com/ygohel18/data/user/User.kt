package com.ygohel18.data.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId val id : ObjectId = ObjectId(),
    val username: String,
    val email: String,
    val password: String,
    val salt: String,
    val status: Boolean = true,
    val newsletter: Boolean = true,
    val points: Int = 0,
    val subscription: String
)
