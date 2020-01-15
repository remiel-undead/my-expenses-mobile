package ru.neofusion.undead.myexpenses.domain

class FilterPanelSettings(
    val dateStart: String?,
    val dateEnd: String?,
    val category: Int?,
    val useSubcategories: Boolean?,
    val period: Period?
)