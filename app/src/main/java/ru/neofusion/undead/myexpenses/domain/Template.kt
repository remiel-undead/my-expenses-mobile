package ru.neofusion.undead.myexpenses.domain

class Template(
    val id: Int,
    val category: Category,
    val description: String?,
    val seller: String?,
    val cost: String
) {
    fun getViewableDescription(): String =
        when {
            description.isNullOrEmpty() -> seller.orEmpty()
            seller.isNullOrEmpty() -> description.orEmpty()
            else -> "$description / $seller"
        }
}