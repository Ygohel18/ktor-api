package com.ygohel18.data.user

import org.bson.types.ObjectId

interface UserDataSource {
    suspend fun getUserByUsername(username: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserById(id: String): User?
    suspend fun insertUser(user: User): Boolean
}