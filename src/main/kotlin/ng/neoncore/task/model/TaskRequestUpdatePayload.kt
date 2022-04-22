package ng.neoncore.task.model

import java.time.LocalDateTime

data class TaskRequestUpdatePayload(
    val taskId: Int,
    val taskDescription: String,
    val isCompleted: Boolean,
    val completeDate: LocalDateTime
)
