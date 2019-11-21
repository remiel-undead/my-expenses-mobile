package ru.neofusion.undead.myexpenses.repository.network

import android.content.Context
import com.google.gson.GsonBuilder
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.neofusion.undead.myexpenses.BuildConfig
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Mapper
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.result.Category as ApiCategory
import ru.neofusion.undead.myexpenses.repository.network.result.Key
import ru.neofusion.undead.myexpenses.repository.network.result.Login
import ru.neofusion.undead.myexpenses.repository.network.result.Order
import ru.neofusion.undead.myexpenses.repository.network.result.Payment as ApiPayment
import ru.neofusion.undead.myexpenses.repository.storage.AuthHelper
import java.util.*

object Api {
    private lateinit var service: MyExpensesService
    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(OkHttpClientProvider.getClient(context))
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setDateFormat("yyyy-MM-dd")
                        .create()
                )
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

    @JvmStatic
    fun getCategories(context: Context): Single<Result<List<Category>>> =
        Single.fromCallable { AuthHelper.getKey(context) ?: "" }
            .map { service.getCategories(it).execute() }
            .map { response ->
                Mapper.responseToResult<List<ApiCategory>, List<Category>>(response) { apiCategories ->
                    apiCategories.map {
                        Category(
                            it.id,
                            it.name,
                            it.parentId,
                            it.hidden
                        )
                    }
                }
            }

    @JvmStatic
    fun getPayments(
        context: Context,
        startDate: Date,
        endDate: Date,
        order: Order,
        categoryId: Int? = null,
        useSubCategories: Boolean = true
    ): Single<Result<List<Payment>>> =
        Single.fromCallable { AuthHelper.getKey(context) ?: "" }
            .map {
                service.getPayments(
                    it,
                    startDate,
                    endDate,
                    order.value,
                    categoryId,
                    if (useSubCategories) 1 else 0
                ).execute()
            }
            .map { response ->
                Mapper.responseToResult<List<ApiPayment>, List<Payment>>(response) { apiPayments ->
                    apiPayments.map {
                        Payment(
                            it.id,
                            it.category,
                            it.categoryId,
                            it.date,
                            it.description,
                            it.seller,
                            it.cost
                        )
                    }
                }
            }
}