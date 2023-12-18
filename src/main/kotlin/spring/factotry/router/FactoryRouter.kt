package spring.factotry.router

import brave.Tracing
import io.micrometer.context.ContextSnapshot
import io.micrometer.tracing.Tracer
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router
import spring.factotry.handler.FactoryHandler

@Configuration
class FactoryRouter(
    private val factoryHandler: FactoryHandler
) {
    @Bean
    fun factorySpecRouter() = router {
        "/api/v1/factory".nest {
            GET("", factoryHandler::findAll)
            GET("/{id}", factoryHandler::findById)
            POST("/{id}/buyOrigin", factoryHandler::buyOrigin)
            POST("/{id}/useOrigin", factoryHandler::useOrigin)
            GET("/{id}/success", factoryHandler::success)
            GET("/{id}/failure", factoryHandler::failure)
        }

        "/login".nest {
            POST("", factoryHandler::login)
        }

        "/logout".nest {
            POST("", factoryHandler::logout)
        }
    }
}