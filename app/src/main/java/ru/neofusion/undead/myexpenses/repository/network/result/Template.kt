package ru.neofusion.undead.myexpenses.repository.network.result

import com.google.gson.annotations.SerializedName

class Template(
    @SerializedName("id") val id: Int,
    @SerializedName("category") val category: Category,
    @SerializedName("description") val description: String?,
    @SerializedName("seller") val seller: String?,
    @SerializedName("cost") val cost: String
)