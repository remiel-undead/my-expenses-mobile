package ru.neofusion.undead.myexpenses.repository.network.result

import com.google.gson.annotations.SerializedName

class Category(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("parentId") val parentId: Int?,
    @SerializedName("hidden") val hidden: Boolean
)