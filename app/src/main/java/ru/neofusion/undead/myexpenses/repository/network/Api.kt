package ru.neofusion.undead.myexpenses.repository.network

import android.content.Context
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.neofusion.undead.myexpenses.BuildConfig
import ru.neofusion.undead.myexpenses.repository.Result
import ru.neofusion.undead.myexpenses.repository.network.result.ApiResult
import ru.neofusion.undead.myexpenses.repository.network.result.Login

object Api {
    private lateinit var service: MyExpensesService
    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(OkHttpClientProvider.getClient(context))
            .addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().create())
            )
            .build()
        service = retrofit
            .create(MyExpensesService::class.java)
    }

    @JvmStatic
    fun login(login: String, password: String): Single<Result<String>> =
        Single.fromCallable { service.login(Login(login, password)).execute() }.map { response ->
            response.takeIf { it.isSuccessful }?.body()?.success?.let {
                Result.Success(it.key)
            } ?: response.errorBody()?.let { it ->
                val errorBody = parseErrorBody(it.string())
                val message = errorBody.validation?.let {
                    it.fold("",
                        { acc, item -> acc + "\n${item.field}: ${item.message}" })
                } ?: errorBody.error?.message ?: return@let null // TODO manage code type
                Result.Error(message)
            } ?: response?.message()?.let {
                Result.Error(it)
            } ?: Result.Error("Ой-ой-ой")
        }

    private fun parseErrorBody(responseBodyString: String) =
        GsonBuilder().create().fromJson(responseBodyString, ApiResult.ApiError::class.java)
}