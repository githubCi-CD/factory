package spring.factotry.webClient

import io.micrometer.tracing.annotation.SpanTag
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spring.factotry.dto.OriginDto

@Component
class StorageWebclient(
    private val webClient: WebClient
) {

    fun getOriginData(id: Long): Mono<OriginDto> =
        webClient.get()
            .uri("/origin/${id}")
            .retrieve()
            .bodyToMono(OriginDto::class.java)
}