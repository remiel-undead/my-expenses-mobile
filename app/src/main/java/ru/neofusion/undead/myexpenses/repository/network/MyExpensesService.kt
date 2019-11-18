package ru.neofusion.undead.myexpenses.repository.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import ru.neofusion.undead.myexpenses.repository.network.result.ApiResult
import ru.neofusion.undead.myexpenses.repository.network.result.Key
import ru.neofusion.undead.myexpenses.repository.network.result.Login

interface MyExpensesService {
    @POST("/api/v1/login")
    fun login(@Body login: Login): Call<ApiResult.ApiSuccess<Key>>

    @GET("/api/v1/logout")
    fun logout(@Header("API-Key") apiKey: String): Call<ApiResult.ApiSuccess<Nothing>>
}