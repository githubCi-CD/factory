package spring.factotry.dto

data class OriginDto (
    val id: Long? = null,
    val count: Int = 0,
    val price: Long = 0,
) {
    fun isBuyOrigin(asset: Long): Boolean =
        if(this.price * this.count <= asset) {
            true
        } else {
            false
        }

    fun getTotalPrice(): Long =
        this.count * this.price
}