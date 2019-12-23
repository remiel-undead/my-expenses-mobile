package ru.neofusion.undead.myexpenses.repository.network.request

import com.google.gson.annotations.SerializedName

class Template(
    @SerializedName("category") val category: Int,
    @SerializedName("description") val description: String,
    @SerializedName("seller") val seller: String,
    @SerializedName("cost") val cost: String
)