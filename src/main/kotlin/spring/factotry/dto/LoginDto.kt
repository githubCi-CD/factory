package spring.factotry.dto

import spring.factotry.model.Factory

data class LoginDto (
    val id: Long? = null,
    val name: String,
) {
    companion object {
        fun from(factory: Factory): LoginDto =
            LoginDto(
                id = factory.id,
                name = factory.name
            )
    }

    fun to(): Factory =
        Factory(
            name = this.name
        )
}