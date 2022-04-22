package ng.neoncore.task.model


data class TaskRequestPayload(
    val taskTitle: String,
    val taskDescription: String,
    val taskLocation: String,
    val dueDate: String,
    val longitude: Double,
    val latitude: Double,
    val isComplete: Boolean,
)
