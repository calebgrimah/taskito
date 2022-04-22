package ng.neoncore

import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.amshove.kluent.`should be`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GetTaskRequestsSpec : Spek({
    describe("GetTaskRequests") {
        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = false)
        with(engine) {
            (environment.config as MapApplicationConfig).apply {
                put("ktor.environment", "test")

            }
        }
        engine.application.module(true)
        with(engine) {
            it("Should be okay to ge the list of tasks") {
                handleRequest(HttpMethod.Get, "api/vi/tasks").apply {
                    response.status().`should be`(HttpStatusCode.OK)
                }
            }
        }
    }
})