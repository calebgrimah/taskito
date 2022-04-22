package ng.neoncore.user.repository

import ng.neoncore.db.DatabaseFactory
import ng.neoncore.db.model.Users
import ng.neoncore.user.model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.koin.java.KoinJavaComponent.inject

interface UserRepository {
    suspend fun addUser(email: String, username: String, passwordHash: String): User?
    suspend fun findUser(userId: Int): User?
    suspend fun findUserByEmail(email: String): User?
}

class UserRepositoryImpl : UserRepository {
    private val databaseFactory: DatabaseFactory by inject(DatabaseFactory::class.java)

    override suspend fun addUser(email: String, username: String, passwordHash: String): User? {
        var statement: InsertStatement<Number>? = null
        databaseFactory.dbQuery {
            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.username] = username
                user[Users.passwordHash] = passwordHash
            }
        }
        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun findUser(userId: Int): User? = databaseFactory.dbQuery {
        Users.select { Users.userId.eq(userId) }.map {
            rowToUser(it)
        }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String): User? = databaseFactory.dbQuery {
        Users.select { Users.email.eq(email) }.map {
            rowToUser(it)
        }.singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
            userId = row[Users.userId],
            email = row[Users.email],
            username = row[Users.username],
            passwordHash = row[Users.passwordHash]
        )
    }
}