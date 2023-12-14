package spring.factotry.webClient

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spring.factotry.dto.OriginDto

@Component
class StorageWebclient(
    @Value("\${storage.url}")
    val storageUrl: String
) {
    private val webClient = WebClient.builder().baseUrl(storageUrl).build()

    fun getOriginData(id: Long): Mono<OriginDto> =
        webClient.get()
            .uri("/origin/${id}")
            .retrieve()
            .bodyToMono(OriginDto::class.java)
}