package ng.neoncore.db.model

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TaskRequests : Table() {
    val taskId: Column<Int> = integer("TASK_ID").autoIncrement()
    val creatorId: Column<Int> = integer("CREATOR_ID").references(Users.userId)
    val performerId: Column<Int> = integer("PERFORMER_ID")//person who performs this task
    val taskTitle = varchar("TASK_TITLE", 256)
    val taskDescription = varchar("TASK_DESCRIPTION", 1024)
    val taskLocation = varchar("TASK_LOCATION", 256)
    val dueDate = varchar("DUE_DATE", 64)
    val creationDate = varchar("CREATION_DATE", 64)
    val longitude: Column<Double> = double("LONGITUDE")
    val latitude: Column<Double> = double("LATITUDE")
    val isCompleted = bool("IS_COMPLETED")
    val completedDate = varchar("COMPLETED_DATE", 64)


    override val primaryKey = PrimaryKey(taskId)
}