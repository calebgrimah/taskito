package ng.neoncore.task.repository

import ng.neoncore.db.DatabaseFactory
import ng.neoncore.db.model.TaskRequests
import ng.neoncore.task.model.TaskRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.koin.java.KoinJavaComponent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskRequestRepositoryImpl : TaskRequestRepository {
    private val databaseFactory: DatabaseFactory by KoinJavaComponent.inject(DatabaseFactory::class.java)

    override suspend fun addTaskRequest(
        userId: Int,
        taskTitle: String,
        taskDescription: String,
        isCompleted: Boolean,
        dueDate: String,
        taskLocation: String,
        longitude: Double,
        latitude: Double
    ): TaskRequest? {
        var statement: InsertStatement<Number>? = null
        databaseFactory.dbQuery {
            statement = TaskRequests.insert {
                it[TaskRequests.creatorId] = userId
                it[TaskRequests.taskTitle] = taskTitle
                it[TaskRequests.taskDescription] = taskDescription
                it[TaskRequests.isCompleted] = isCompleted
                it[TaskRequests.taskLocation] = taskLocation
                it[TaskRequests.longitude] = longitude
                it[TaskRequests.latitude] = latitude
                it[TaskRequests.creationDate] = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                it[TaskRequests.dueDate] = dueDate
                it[TaskRequests.performerId] = -1
                it[TaskRequests.completedDate] = ""
            }
        }
        return rowToTaskRequest(statement?.resultedValues?.get(0))
    }

    override suspend fun getTaskRequest(taskId: Int): TaskRequest? {
        return databaseFactory.dbQuery {
            TaskRequests.select {
                TaskRequests.taskId.eq(taskId)
            }.map { rowToTaskRequest(it) }.singleOrNull()
        }
    }

    override suspend fun updateTaskRequest(
        taskId: Int,
        taskDescription: String,
        isCompleted: Boolean,
        completeDate: LocalDateTime
    ): Boolean {
        return databaseFactory.dbQuery {
            TaskRequests.update({ TaskRequests.taskId eq taskId }) {
                it[TaskRequests.taskDescription] = taskDescription
                it[TaskRequests.isCompleted] = isCompleted
                it[TaskRequests.completedDate] = completeDate.format(DateTimeFormatter.ISO_DATE_TIME)
            } > 0
        }
    }

    override suspend fun deleteTaskRequest(taskId: Int): Boolean {
        return databaseFactory.dbQuery {
            TaskRequests.deleteWhere { TaskRequests.taskId eq taskId } > 0
        }
    }

    override suspend fun getTaskRequests(userId: Int): List<TaskRequest> {
        return databaseFactory.dbQuery {
            TaskRequests.select {
                TaskRequests.creatorId eq userId
            }.mapNotNull { rowToTaskRequest(it) }
        }
    }

    override suspend fun assignTaskRequestToPerformer(performerId: Int, taskId: Int): Boolean {
        return databaseFactory.dbQuery {
            TaskRequests.update({ TaskRequests.taskId eq taskId }) {
                it[TaskRequests.performerId] = performerId
            } > 0
        }
    }

    private fun rowToTaskRequest(row: ResultRow?): TaskRequest? {
        if (row == null) {
            return null
        }
        return TaskRequest(
            taskId = row[TaskRequests.taskId],
            creatorId = row[TaskRequests.creatorId],
            taskTitle = row[TaskRequests.taskTitle],
            taskDescription = row[TaskRequests.taskDescription],
            dueDate = LocalDateTime.parse(row[TaskRequests.dueDate], DateTimeFormatter.ISO_DATE_TIME),
            taskLocation = row[TaskRequests.taskLocation],
            creationDate = LocalDateTime.parse(row[TaskRequests.creationDate], DateTimeFormatter.ISO_DATE_TIME),
            longitude = row[TaskRequests.longitude],
            latitude = row[TaskRequests.latitude],
            isComplete = row[TaskRequests.isCompleted],
            performerId = row[TaskRequests.performerId],
            completedDate = (if (row[TaskRequests.completedDate].isEmpty()) null else LocalDateTime.parse(
                row[TaskRequests.completedDate],
                DateTimeFormatter.ISO_DATE_TIME
            )),
        )
    }
}