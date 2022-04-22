package ng.neoncore.task.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.locations.put
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

import ng.neoncore.API_VERSION
import ng.neoncore.auth.MySession
import ng.neoncore.task.model.TaskRequestAssignmentPayload
import ng.neoncore.task.model.TaskRequestPayload
import ng.neoncore.task.model.TaskRequestUpdatePayload
import ng.neoncore.task.repository.TaskRequestRepository
import ng.neoncore.user.repository.UserRepository
import ng.neoncore.util.inject
import java.time.format.DateTimeFormatter

const val TASK_REQUEST_REQUESTS = "$API_VERSION/tasks"
const val SINGLE_TASK_REQUEST = "$TASK_REQUEST_REQUESTS/id"
const val ASSIGN_TASK_REQUEST = "$TASK_REQUEST_REQUESTS/assign"

@KtorExperimentalLocationsAPI
@Location(TASK_REQUEST_REQUESTS)
class TaskRequestsRoute

@KtorExperimentalLocationsAPI
@Location(SINGLE_TASK_REQUEST)
class SingleTaskRequestRoute

@KtorExperimentalLocationsAPI
@Location(ASSIGN_TASK_REQUEST)
class AssignTaskRequestRoute

@KtorExperimentalLocationsAPI
fun Route.configureTaskRequests() {
    val taskRequestRepository: TaskRequestRepository by inject()
    val userRepository: UserRepository by inject()

    authenticate("jwt") {
        post<TaskRequestsRoute> {
            val taskRequestsParameters = call.receive<TaskRequestPayload>()
            val user = call.sessions.get<MySession>()?.let {
                print("dundund${it.userId}")
                userRepository.findUser(it.userId)
            }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems Retrieving User")
                return@post
            }
            try {
                val currentTaskRequest = taskRequestRepository.addTaskRequest(
                    userId = user.userId,
                    taskTitle = taskRequestsParameters.taskTitle,
                    taskDescription = taskRequestsParameters.taskDescription,
                    isCompleted = taskRequestsParameters.isComplete,
                    dueDate = taskRequestsParameters.dueDate.format(DateTimeFormatter.ISO_DATE_TIME),
                    taskLocation = taskRequestsParameters.taskLocation,
                    longitude = taskRequestsParameters.longitude,
                    latitude = taskRequestsParameters.latitude
                )
                currentTaskRequest?.taskId?.let {
                    call.respond(HttpStatusCode.Created, currentTaskRequest)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add task request", e)
                call.respond(HttpStatusCode.BadRequest, "Problems saving task request!")
            }
        }

        get<TaskRequestsRoute> {
            val user = call.sessions.get<MySession>()?.let {
                userRepository.findUser(it.userId)
            }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val tasks = taskRequestRepository.getTaskRequests(userId = user.userId)
                call.respond(HttpStatusCode.OK, tasks)
                return@get
            } catch (e: Throwable) {
                application.log.error("Failed to get task requests.", e)
                call.respond(HttpStatusCode.BadRequest, "Problems fetching task requests.")
            }
        }

        delete<SingleTaskRequestRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@delete
            }
            val taskId = call.parameters["id"] ?: throw IllegalArgumentException("Missing task request id")
            try {
                val successfull = taskRequestRepository.deleteTaskRequest(taskId = taskId.toInt());//todo:review
                call.respond(HttpStatusCode.OK, successfull)
            } catch (e: Throwable) {
                application.log.error("Failed to delete task requests.")
                call.respond(HttpStatusCode.BadRequest, "Problems deleting task requests.")
            }
        }

        get<SingleTaskRequestRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            val taskRequestId = call.parameters["id"] ?: throw IllegalArgumentException("Missing task request id")
            try {
                val taskRequest = taskRequestRepository.getTaskRequest(taskId = taskRequestId.toInt())
                taskRequest?.taskId?.let {
                    call.respond(HttpStatusCode.OK, taskRequest)
                }
                call.respond(HttpStatusCode.NotFound, "No task associated with id")
                return@get
            } catch (e: Throwable) {
                application.log.error("Failed to find task request.")
                call.respond(HttpStatusCode.BadRequest, "Problems finding task request.")
            }
        }

        put<AssignTaskRequestRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@put
            }
            val taskRequestsParameters = call.receive<TaskRequestAssignmentPayload>()
            try {
                val taskRequest = taskRequestRepository.assignTaskRequestToPerformer(
                    taskId = taskRequestsParameters.taskId,
                    performerId = taskRequestsParameters.performerUserId
                )
                call.respond(HttpStatusCode.OK, taskRequest)
            } catch (e: Throwable) {
                application.log.error("Failed to assign task request.")
                call.respond(HttpStatusCode.BadRequest, "Problems assigning task request to performer.")
            }
        }

        put<SingleTaskRequestRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@put
            }
            val taskRequestsParameters = call.receive<TaskRequestUpdatePayload>()
            try {
                val taskRequest = taskRequestRepository.updateTaskRequest(
                    taskId = taskRequestsParameters.taskId,
                    taskDescription = taskRequestsParameters.taskDescription,
                    isCompleted = taskRequestsParameters.isCompleted,
                    completeDate = taskRequestsParameters.completeDate
                )
                call.respond(HttpStatusCode.OK, taskRequest)
            } catch (e: Throwable) {
                application.log.error("Failed to update task request.")
                call.respond(HttpStatusCode.BadRequest, "Problems updating  task request.")
            }
        }

    }

}