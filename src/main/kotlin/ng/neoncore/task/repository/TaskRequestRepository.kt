package ng.neoncore.task.repository

import ng.neoncore.task.model.TaskRequest
import java.time.LocalDateTime

interface TaskRequestRepository {
    suspend fun addTaskRequest(
        userId: Int,
        taskTitle: String,
        taskDescription: String,
        isCompleted: Boolean,
//        @JsonSerialize(using = ToStringSerializer::class)
//        @JsonDeserialize(using = FromStringDeserializer::class)
        dueDate: String,
        taskLocation: String,
        longitude: Double,
        latitude: Double,
    ): TaskRequest?

    suspend fun getTaskRequest(taskId: Int): TaskRequest?
    suspend fun updateTaskRequest(
        taskId: Int,
        taskDescription: String,
        isCompleted: Boolean,
        completeDate: LocalDateTime
    ): Boolean

    suspend fun deleteTaskRequest(taskId: Int): Boolean
    suspend fun getTaskRequests(userId: Int): List<TaskRequest>

    suspend fun assignTaskRequestToPerformer(performerId: Int, taskId: Int): Boolean
}