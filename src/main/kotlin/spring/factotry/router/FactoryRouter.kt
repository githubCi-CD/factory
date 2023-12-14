package spring.factotry.router

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
        }

        "/login".nest {
            POST("", factoryHandler::login)
        }

        "/logout".nest {
            POST("", factoryHandler::logout)
        }
    }
}