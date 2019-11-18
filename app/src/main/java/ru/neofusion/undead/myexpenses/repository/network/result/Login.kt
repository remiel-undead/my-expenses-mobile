package ru.neofusion.undead.myexpenses.repository.network.result

import com.google.gson.annotations.SerializedName

class Login(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)