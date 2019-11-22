package ru.neofusion.undead.myexpenses.repository.network.request

import com.google.gson.annotations.SerializedName
import java.util.*

class Payment(
    @SerializedName("category") val category: Int,
    @SerializedName("date") val date: Date,
    @SerializedName("description") val description: String?,
    @SerializedName("seller") val seller: String?,
    @SerializedName("cost") val cost: Int
)