package ru.neofusion.undead.myexpenses.repository.network

import retrofit2.Call
import retrofit2.http.*
import ru.neofusion.undead.myexpenses.repository.network.result.*
import java.util.*

interface MyExpensesService {
    @POST("/api/v1/login")
    fun login(@Body login: Login): Call<ApiResult<Key>>

    @GET("/api/v1/logout")
    fun logout(@Header("API-Key") apiKey: String): Call<ApiResult<Nothing>>

    @GET("/api/v1/categories")
    fun getCategories(@Header("API-Key") apiKey: String): Call<ApiResult<List<Category>>>

    @GET("/api/v1/payments")
    fun getPayments(
        @Header("API-Key") apiKey: String,
        @Query("start") start: Date,
        @Query("end") end: Date,
        @Query("order") order: Int,
        @Query("cat") cat: Int? = null,
        @Query("sub") sub: Int = 1
    ): Call<ApiResult<List<Payment>>>
}