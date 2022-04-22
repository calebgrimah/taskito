package ng.neoncore.task.model

data class TaskRequestAssignmentPayload(
    val taskId: Int,
    val performerUserId: Int,
)