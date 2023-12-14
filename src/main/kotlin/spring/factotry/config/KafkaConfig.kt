package spring.factotry.config

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.ssl.SslBundles
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.kafka.sender.SenderOptions
import spring.factotry.dto.StorageDto

@Configuration
class KafkaConfig {

    @Bean
    fun reactiveKafkaProducerTemplate(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, StorageDto> {
        val props = properties.buildProducerProperties()
        return ReactiveKafkaProducerTemplate<String, StorageDto>(SenderOptions.create(props))
    }
}