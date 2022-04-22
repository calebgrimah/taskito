package ng.neoncore

import io.ktor.server.application.*
import ng.neoncore.plugins.configureKoin
import ng.neoncore.plugins.configureRouting
import ng.neoncore.plugins.configureSecurity
import ng.neoncore.plugins.configureSerialization


fun main(args: Array<String>): Unit {
    io.ktor.server.cio.EngineMain.main(args)
}


@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module(testing: Boolean = false) {
    configureKoin()
    configureSecurity()
    configureSerialization()
    configureRouting()
}


const val API_VERSION = "/v1"
