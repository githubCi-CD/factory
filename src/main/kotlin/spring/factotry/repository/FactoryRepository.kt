package spring.factotry.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import spring.factotry.model.Factory

@Repository
interface FactoryRepository: ReactiveCrudRepository<Factory, Long> {

    fun findFactoryByName(id: String): Mono<Factory>

    @Query(
        """
            UPDATE factory
            SET status = false
            WHERE id = :id
        """
    )
    fun updateFactoryStatusById(id: Long?): Mono<Factory>

    @Query(
        """
            UPDATE factory
            SET success_count = success_count + 1
            where id = :id
        """
    )
    fun updateFactorySuccessById(id: Long?): Mono<Factory>

    @Query(
        """
            UPDATE factory
            SET failure_count = failure_count + 1
            where id = :id
        """
    )
    fun updateFactoryFailureById(id: Long?): Mono<Factory>
}