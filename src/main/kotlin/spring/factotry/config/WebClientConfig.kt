package spring.factotry.config

import io.micrometer.tracing.SpanName
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    @Value("\${storage.url}")
    private val storageUrl: String
) {

    @Bean
    fun webClient(webclient: WebClient.Builder): WebClient =
        webclient.baseUrl(storageUrl).build()
}