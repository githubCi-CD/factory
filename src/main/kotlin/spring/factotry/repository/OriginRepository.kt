package spring.factotry.repository

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spring.factotry.webClient.StorageWebclient

@Component
class OriginRepository(
    private val storageWebclient: StorageWebclient,
) {

    fun getOriginPrice(id: Long): Mono<Long> =
            storageWebclient.getOriginData(id)
                .map { origin -> origin.price }

}