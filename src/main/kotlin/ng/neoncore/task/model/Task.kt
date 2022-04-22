package ng.neoncore.task.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.LocalDateTime

data class TaskRequest(
    val taskId: Int,
    val creatorId: Int,
    val performerId: Int,
    val taskTitle: String,
    val taskDescription: String,
    val taskLocation: String,
    @JsonSerialize(using = ToStringSerializer::class)
    @JsonDeserialize(using = FromStringDeserializer::class)
    val dueDate: LocalDateTime,
    @JsonSerialize(using = ToStringSerializer::class)
    @JsonDeserialize(using = FromStringDeserializer::class)
    val creationDate: LocalDateTime = LocalDateTime.now(),
    val longitude: Double,
    val latitude: Double,
    val isComplete: Boolean,
    val completedDate: LocalDateTime?
)
