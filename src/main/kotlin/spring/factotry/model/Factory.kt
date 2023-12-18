package spring.factotry.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("factory")
data class Factory(
    @Id
    val id: Long? = null,
    val name: String,
    val income: Long = 0,
    val outcome: Long = 0,
    val asset: Long = 50000000,
    val failureCount: Int = 0,
    val successCount: Int = 0 ,
    val status: Boolean = true,
)
