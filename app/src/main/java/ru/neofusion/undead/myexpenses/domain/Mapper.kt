package ru.neofusion.undead.myexpenses.domain

import com.google.gson.GsonBuilder
import retrofit2.Response
import ru.neofusion.undead.myexpenses.repository.network.result.ApiResult

object Mapper {
    private const val CODE_INVALID_LOGIN_PASSWORD = 100
    private const val CODE_KEY_LIMIT_IS_OUT = 101
    private const val CODE_KEY_GENERATION_ERROR = 102

    fun <T : Any?, R : Any?> responseToResult(
        response: Response<ApiResult<T>>,
        mapper: ((T) -> R)? = null
    ): Result<R> =
        getSuccess(response)?.let {
            Result.Success(mapper?.invoke(it) ?: it as R)
        } ?: takeIf { response.code() == 401 }?.let {
            Result.Error(
                getErrorMessage(
                    response
                ), UnauthorizedException()
            )
        } ?: run {
            val errorBody = response.errorBody()?.string()?.let {
                parseErrorBody(
                    it
                )
            }
            errorBody ?: return@run null

            val validationResult =
                 getValidationPairs(errorBody.validation)?.let { validationPairs ->
                    Result.Error(
                        "Ошибка валидации",
                        ValidationException(validationPairs)
                    )
                }
            if (validationResult != null) return@run validationResult

            val exceptionMessage = errorBody.error?.message ?: getErrorMessage(
                response
            )
            when (errorBody.error?.code) {
                CODE_INVALID_LOGIN_PASSWORD -> Result.Error(
                    exceptionMessage,
                    SecurityException.InvalidLoginPassword()
                )
                CODE_KEY_LIMIT_IS_OUT -> Result.Error(
                    exceptionMessage,
                    SecurityException.KeyLimitIsOut()
                )
                CODE_KEY_GENERATION_ERROR -> Result.Error(
                    exceptionMessage,
                    SecurityException.KeyGenerationError()
                )
                else -> null
            }
        } ?: Result.Error(getErrorMessage(response))

    private fun <T : Any?> getSuccess(response: Response<ApiResult<T>>): T? =
        response.takeIf { response.isSuccessful }?.body()?.success

    private fun getValidationPairs(validation: List<ApiResult.ValidationItem>?)
            : List<Pair<String, String>>? =
        validation?.map { it.field to it.message }

    private fun <T : Any?> getErrorMessage(response: Response<ApiResult<T>>) =
        response.message() ?: "Ой-ой-ой"

    private fun parseErrorBody(responseBodyString: String): ApiResult.ErrorBody? =
        try {
            GsonBuilder().create()
                .fromJson(responseBodyString, ApiResult.ErrorBody::class.java)
        } catch (e: Exception) {
            null
        }
}