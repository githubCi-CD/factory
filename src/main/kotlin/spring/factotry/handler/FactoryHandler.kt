package spring.factotry.handler

import io.micrometer.tracing.Span
import io.micrometer.tracing.Tracer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import spring.factotry.dto.LoginDto
import spring.factotry.dto.OriginDto
import spring.factotry.dto.StorageDto
import spring.factotry.model.Factory
import spring.factotry.repository.FactoryRepository
import spring.factotry.repository.KafkaProducer
import spring.factotry.repository.OriginRepository

@Component
class FactoryHandler(
    private val factoryRepository: FactoryRepository,
    private val originRepository: OriginRepository,
    private val kafkaProducer: KafkaProducer,
    @Value("\${spring.kafka.topic}")
    private val topic: String
) {

    private val log = LoggerFactory.getLogger(FactoryHandler::class.java)
    fun findAll(serverRequest: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body(factoryRepository.findAll(), Factory::class.java)
            .doOnNext { log.info("factory find all") }

    fun findById(serverRequest: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(factoryRepository.findById(serverRequest.pathVariable("id").toLong()), Factory::class.java)

    @Transactional
    fun buyOrigin(serverRequest: ServerRequest): Mono<ServerResponse> =
        factoryRepository.findById(serverRequest.pathVariable("id").toLong())
            .flatMap { factory ->
                val asset = factory.asset
                serverRequest.bodyToMono(OriginDto::class.java)
                    .flatMap { originDto ->
                        originRepository.getOriginPrice(originDto.id ?: throw IllegalArgumentException("origin id is null"))
                            .flatMap {
                                val modifiedOriginDto = originDto.copy(
                                    price = it
                                )
                                    if (modifiedOriginDto.isBuyOrigin(asset)) {
                                        val paidMoney = modifiedOriginDto.getTotalPrice()
                                        factoryRepository.save(
                                            factory.copy(
                                                asset = factory.asset - paidMoney,
                                                outcome = factory.outcome + paidMoney
                                            )
                                        ).flatMap { savedFactory ->
                                            kafkaProducer.sendMessage<Factory>(
                                                topic,
                                                StorageDto(factoryId = savedFactory.id ?: 0, originId = modifiedOriginDto.id, count = modifiedOriginDto.count),
                                                savedFactory
                                            ).toMono()
                                        }
                                    } else {
                                        Mono.error(IllegalArgumentException("asset is overloaded"))
                                    }
                            }.flatMap { result ->
                                ServerResponse.ok().body(result.toMono(), Factory::class.java)
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
                            .flatMap { savedFatory ->
                                    kafkaProducer.sendMessage<Factory>(
                                        topic,
                                        StorageDto(factoryId = savedFatory.id ?: 0, count = 5000),
                                        savedFatory
                                    ).toMono()
                            }
                    )
                    .flatMap { factory ->
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