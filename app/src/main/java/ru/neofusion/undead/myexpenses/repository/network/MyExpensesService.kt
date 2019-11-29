package ru.neofusion.undead.myexpenses.repository.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ru.neofusion.undead.myexpenses.repository.network.result.*

interface MyExpensesService {
    @POST("/api/v1/login")
    fun login(@Body login: Login): Call<ApiResult<Key>>

    @GET("/api/v1/logout")
    fun logout(@Header("API-Key") apiKey: String): Call<ApiResult<Nothing>>

    @GET("/api/v1/categories")
    fun getCategories(@Header("API-Key") apiKey: String): Call<ApiResult<List<Category>>>

    @POST("/api/v1/categories")
    fun addCategory(
        @Header("API-Key") apiKey: String,
        @Body category: ru.neofusion.undead.myexpenses.repository.network.request.Category
    ): Call<ApiResult<Id>>

    @PUT("/api/v1/categories/{id}")
    fun editCategory(
        @Header("API-Key") apiKey: String,
        @Path("id") id: Int,
        @Body category: ru.neofusion.undead.myexpenses.repository.network.request.Category
    ): Call<ApiResult<Nothing>>

    @GET("/api/v1/categories/{id}")
    fun getCategory(
        @Header("API-Key") apiKey: String,
        @Path("id") id: Int
    ): Call<ApiResult<Category>>

    @GET("/api/v1/payments")
    fun getPayments(
        @Header("API-Key") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("order") order: Int,
        @Query("cat") cat: Int? = null,
        @Query("sub") sub: Int = 1
    ): Call<ApiResult<List<Payment>>>

    @POST("/api/v1/payments")
    fun addPayment(
        @Header("API-Key") apiKey: String,
        @Body payment: ru.neofusion.undead.myexpenses.repository.network.request.Payment
    ): Call<ApiResult<Id>>

    @PUT("/api/v1/payments/{id}")
    fun editPayment(
        @Header("API-Key") apiKey: String,
        @Path("id") id: Int,
        @Body payment: ru.neofusion.undead.myexpenses.repository.network.request.Payment
    ): Call<ApiResult<Nothing>>

    @GET("/api/v1/payments/{id}")
    fun getPayment(
        @Header("API-Key") apiKey: String,
        @Path("id") id: Int
    ): Call<ApiResult<Payment>>

    @DELETE("/api/v1/payments/{id}")
    fun deletePayment(
        @Header("API-Key") apiKey: String,
        @Path("id") id: Int
    ): Call<ApiResult<Nothing>>
}