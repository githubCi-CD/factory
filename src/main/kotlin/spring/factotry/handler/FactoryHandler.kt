package spring.factotry.handler

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import spring.factotry.dto.LoginDto
import spring.factotry.dto.OriginDto
import spring.factotry.model.Factory
import spring.factotry.repository.FactoryRepository
import spring.factotry.repository.OriginRepository

@Component
class FactoryHandler(
    private val factoryRepository: FactoryRepository,
    private val originRepository: OriginRepository
) {

    fun findAll(serverRequest: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body(factoryRepository.findAll(), Factory::class.java)

    fun findById(serverRequest: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body(factoryRepository.findById(serverRequest.pathVariable("id").toLong()), Factory::class.java)

    @Transactional
    fun buyOrigin(serverRequest: ServerRequest): Mono<ServerResponse> =
        factoryRepository.findById(serverRequest.pathVariable("id").toLong())
            .flatMap { factory ->
                val asset = factory.asset
                serverRequest.bodyToMono(OriginDto::class.java)
                    .flatMap { originDto ->
                        originRepository.getOriginPrice(originDto.id ?: throw IllegalArgumentException("error"))
                            .flatMap {
                                originDto.copy(
                                    price = it
                                ).let { originDto ->
                                    if (originDto.isBuyOrigin(asset)) {
                                        val paidMoney = originDto.getTotalPrice()
                                        factoryRepository.save(
                                            factory.copy(
                                                asset = factory.asset - paidMoney,
                                                outcome = factory.outcome + paidMoney
                                            )
                                        )
                                        //todo: 자원 증가 이벤트 전송
                                    } else {
                                        throw IllegalArgumentException("Can not buy")
                                    }
                            }
                        }.let { result ->
                            ServerResponse.ok().body(result, Factory::class.java)
                        }
                    }.onErrorResume { error ->
                        ServerResponse.badRequest().bodyValue("Error: ${error.message}")
                    }
            }

    fun login(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono(LoginDto::class.java)
            .flatMap { loginDto ->
                factoryRepository.findFactoryByName(loginDto.name)
                    .switchIfEmpty(
                        factoryRepository.save(loginDto.to())
                    )
                    .flatMap { factory ->
                        //todo: 자원증가 이벤트 전송
                        ServerResponse.ok().body(LoginDto.from(factory).toMono(), LoginDto::class.java)
                    }
            }

    fun logout(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono(LoginDto::class.java)
            .flatMap { loginDto ->
                factoryRepository.findFactoryByName(loginDto.name)
                    .flatMap { factory ->
                        ServerResponse.ok().bodyValue(factoryRepository.updateFactoryStatusById(factory.id))
                    }
            }
}