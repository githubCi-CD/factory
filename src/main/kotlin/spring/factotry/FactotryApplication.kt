package spring.factotry

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FactotryApplication


fun main(args: Array<String>) {
    runApplication<FactotryApplication>(*args)
}
