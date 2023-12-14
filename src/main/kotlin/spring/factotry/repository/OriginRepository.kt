package spring.factotry.repository

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import spring.factotry.webClient.StorageWebclient

@Component
class OriginRepository(
    private val storageWebclient: StorageWebclient
) {

    companion object {
        val originInfo = mutableMapOf<Long, Mono<Long>>()
    }

    fun getOriginPrice(id: Long): Mono<Long> =
        originInfo.computeIfAbsent(id) {
            storageWebclient.getOriginData(id)
                .map { origin -> origin.price }
                .switchIfEmpty(Mono.error(IllegalArgumentException("Failed to retrieve origin price for id: $id")))
        }

}