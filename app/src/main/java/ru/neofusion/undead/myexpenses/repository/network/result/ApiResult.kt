package ru.neofusion.undead.myexpenses.repository.network.result

import com.google.gson.annotations.SerializedName

sealed class ApiResult<out T : Any?> {
    data class ApiSuccess<out T : Any?>(
        @SerializedName("success") val success: T
    ) : ApiResult<T>()

    data class ApiError(
        @SerializedName("error") val error: Error?,
        @SerializedName("validation") val validation: List<ValidationItem>?
    ) : ApiResult<Nothing>()
}

class Error(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String
)

class ValidationItem(
    @SerializedName("field") val field: String,
    @SerializedName("message") val message: String
)