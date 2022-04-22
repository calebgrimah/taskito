package ng.neoncore.db.model

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId: Column<Int> = integer("USER_ID").autoIncrement()
    val email = varchar("EMAIL", 256).uniqueIndex()
    val username = varchar("USERNAME", 256)
    val passwordHash = varchar("PASSWORD_HASH", 64)
    override val primaryKey = PrimaryKey(userId)
}