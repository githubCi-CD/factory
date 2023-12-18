package spring.factotry

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks

@SpringBootApplication
class FactotryApplication


fun main(args: Array<String>) {
    runApplication<FactotryApplication>(*args)
}
