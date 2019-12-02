package ru.neofusion.undead.myexpenses.domain

class Category(
    val id: Int,
    val name: String,
    val parentId: Int?,
    val parentName: String,
    val hidden: Boolean
) {
    fun getViewableName() = if (parentName.isNotEmpty()) "$parentName > $name" else name
}