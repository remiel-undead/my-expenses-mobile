package ru.neofusion.undead.myexpenses.repository.network.result

import com.google.gson.annotations.SerializedName

class ApiResult<out T : Any?>(
    @SerializedName("success") val success: T
) {
    class ValidationItem(
        @SerializedName("field") val field: String,
        @SerializedName("message") val message: String
    )

    class ErrorBody(
        @SerializedName("error") val error: Error?,
        @SerializedName("validation") val validation: List<ValidationItem>?
    )

    class Error(
        @SerializedName("code") val code: Int,
        @SerializedName("message") val message: String
    )
}