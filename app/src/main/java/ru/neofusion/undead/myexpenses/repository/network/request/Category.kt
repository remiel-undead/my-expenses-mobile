package ru.neofusion.undead.myexpenses.repository.network.request

import com.google.gson.annotations.SerializedName

class Category(
    @SerializedName("name") val name: String,
    @SerializedName("parent") val parent: Int?,
    @SerializedName("hidden") val hidden: Boolean
)