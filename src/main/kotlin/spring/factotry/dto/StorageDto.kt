package spring.factotry.dto

data class StorageDto (
    val factoryId: Long,
    val originId: Long? = null,
    val count: Int
)