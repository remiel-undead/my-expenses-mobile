package ru.neofusion.undead.myexpenses.domain

class Category(
    val id: Int,
    val name: String,
    val parentId: Int?,
    val hidden: Boolean
)