package spring.factotry.repository

import brave.Tracer
import io.micrometer.tracing.ScopedSpan
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spring.factotry.webClient.StorageWebclient

@Component
class OriginRepository(
    private val storageWebclient: StorageWebclient,
) {
    private val log = LoggerFactory.getLogger(OriginRepository::class.java)
    companion object {
        val originInfo = mutableMapOf<Long, Mono<Long>>()
    }


    fun getOriginPrice(id: Long): Mono<Long> =
        originInfo.computeIfAbsent(id) {
            storageWebclient.getOriginData(id)
                .map { origin -> origin.price }
                .switchIfEmpty(Mono.error(IllegalArgumentException("origin is not existed")))
        }

}