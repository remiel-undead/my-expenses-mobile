package ru.neofusion.undead.myexpenses.repository.network

import android.content.Context
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.neofusion.undead.myexpenses.BuildConfig
import ru.neofusion.undead.myexpenses.repository.Mapper
import ru.neofusion.undead.myexpenses.repository.Result
import ru.neofusion.undead.myexpenses.repository.network.result.Key
import ru.neofusion.undead.myexpenses.repository.network.result.Login
import ru.neofusion.undead.myexpenses.repository.storage.AuthHelper

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
        Single.fromCallable { service.login(Login(login, password)).execute() }
            .map { response ->
                Mapper.responseToResult<Key, String>(response) { it.key }
            }

    @JvmStatic
    fun logout(context: Context): Single<Result<Nothing>> =
        Single.fromCallable { AuthHelper.getKey(context) ?: "" }
            .map { service.logout(it).execute() }
            .map { response ->
                Mapper.responseToResult<Nothing, Nothing>(response)
            }
}