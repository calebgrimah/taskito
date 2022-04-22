package ng.neoncore.plugins

import io.ktor.server.application.*
import ng.neoncore.auth.JwtService
import ng.neoncore.db.DatabaseFactory
import ng.neoncore.task.repository.TaskRequestRepository
import ng.neoncore.task.repository.TaskRequestRepositoryImpl
import ng.neoncore.user.repository.UserRepository
import ng.neoncore.user.repository.UserRepositoryImpl
import ng.neoncore.util.Koin
import org.koin.dsl.module
import org.koin.logger.SLF4JLogger

val taskitoAppModule = module {
    single { DatabaseFactory() }
    single<UserRepository> { UserRepositoryImpl() }
    single<TaskRequestRepository> { TaskRequestRepositoryImpl() }
    single { JwtService() }
}

fun Application.configureKoin() {
    install(Koin) {
        SLF4JLogger()
        modules = arrayListOf(
            taskitoAppModule,

            )
    }
}