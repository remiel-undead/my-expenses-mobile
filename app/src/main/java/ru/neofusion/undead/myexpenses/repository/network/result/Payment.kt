package ru.neofusion.undead.myexpenses.repository.network.result

import com.google.gson.annotations.SerializedName
import java.util.*

class Payment(
    @SerializedName("id") val id: Int,
    @SerializedName("category") val category: Category,
    @SerializedName("date") val date: Date,
    @SerializedName("description") val description: String?,
    @SerializedName("seller") val seller: String?,
    @SerializedName("cost") val cost: String
)