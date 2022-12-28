package com.ygohel18.data.user

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.bson.types.ObjectId
import org.litote.kmongo.KMongo
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

class MongoUserDataSource(
    db: MongoDatabase
) : UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun getUserByUsername(username: String): User? {
        return users.findOne(User::username eq username)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun getUserById(id: String): User? {
        return users.findOne(User::id eq ObjectId(id))
    }

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

}