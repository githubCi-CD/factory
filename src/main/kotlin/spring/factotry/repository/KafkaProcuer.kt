package spring.factotry.repository

import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component
import spring.factotry.dto.StorageDto

@Component
class KafkaProducer(
    private val kafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, StorageDto>
) {
    fun <T> sendMessage(topic: String, message: StorageDto, origin: T): T {
        kafkaProducerTemplate.send(topic, message).subscribe()
        return origin
    }
}