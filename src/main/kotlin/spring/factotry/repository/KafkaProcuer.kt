package spring.factotry.repository

import brave.Tracer
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component
import spring.factotry.dto.StorageDto
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

@Component
class KafkaProducer(
    private val kafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, StorageDto>,
    private val tracer: Tracer
) {
    @KafkaHandler
    fun <T> sendMessage(topic: String, message: StorageDto, origin: T): T {
        val span = tracer.nextSpan().name("kafka-producer").remoteServiceName("kafka").start()
        kafkaProducerTemplate.send(topic, (message.factoryId%2).toInt(), UUID.randomUUID().toString(),message).subscribe()
        span.finish()
        return origin
    }
}