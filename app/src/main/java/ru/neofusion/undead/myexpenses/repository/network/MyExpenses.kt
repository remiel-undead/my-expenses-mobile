package ru.neofusion.undead.myexpenses.repository.network

import android.content.Context
import com.google.gson.GsonBuilder
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.neofusion.undead.myexpenses.BuildConfig
import ru.neofusion.undead.myexpenses.DateUtils.formatToString
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Mapper
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.result.Id
import ru.neofusion.undead.myexpenses.repository.network.result.Category as ApiCategory
import ru.neofusion.undead.myexpenses.repository.network.result.Key
import ru.neofusion.undead.myexpenses.repository.network.result.Login
import ru.neofusion.undead.myexpenses.repository.network.result.Order
import ru.neofusion.undead.myexpenses.repository.network.result.Payment as ApiPayment
import ru.neofusion.undead.myexpenses.repository.network.request.Category as RequestCategory
import ru.neofusion.undead.myexpenses.repository.network.request.Payment as RequestPayment
import ru.neofusion.undead.myexpenses.repository.storage.AuthHelper
import java.util.*

object MyExpenses {
    private lateinit var service: MyExpensesService
    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(OkHttpClientProvider.getClient(context))
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .serializeNulls()
                        .setDateFormat("yyyy-MM-dd")
                        .create()
                )
            )
            .build()
        service = retrofit
            .create(MyExpensesService::class.java)
    }

    object AuthApi {
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

    object CategoryApi {
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
        fun addCategory(
            context: Context,
            name: String,
            parentId: Int?,
            isHidden: Boolean
        ): Single<Result<Int>> =
            Single.fromCallable { AuthHelper.getKey(context) }
                .map { apiKey ->
                    service.addCategory(
                        apiKey,
                        RequestCategory(
                            name,
                            parentId,
                            isHidden
                        )
                    ).execute()
                }
                .map { response ->
                    Mapper.responseToResult<Id, Int>(response) {
                        it.id
                    }
                }

        @JvmStatic
        fun editCategory(
            context: Context,
            id: Int,
            name: String,
            parentId: Int?,
            isHidden: Boolean
        ): Single<Result<Int>> =
            Single.fromCallable { AuthHelper.getKey(context) }
                .map { apiKey ->
                    service.editCategory(
                        apiKey,
                        id,
                        RequestCategory(
                            name,
                            parentId,
                            isHidden
                        )
                    ).execute()
                }
                .map { response ->
                    Mapper.responseToResult<Nothing, Nothing>(response)
                }

        @JvmStatic
        fun getCategory(
            context: Context,
            categoryId: Int
        ): Single<Result<Category>> =
            Single.fromCallable { AuthHelper.getKey(context) }
                .map { apiKey ->
                    service.getCategory(
                        apiKey,
                        categoryId
                    ).execute()
                }
                .map { response ->
                    Mapper.responseToResult<ApiCategory, Category>(response) {
                        Category(
                            it.id,
                            it.name,
                            it.parentId,
                            it.hidden
                        )
                    }
                }
    }

    object PaymentApi {
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
                        startDate.formatToString(),
                        endDate.formatToString(),
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

        @JvmStatic
        fun addPayment(
            context: Context,
            categoryId: Int,
            date: Date,
            description: String,
            seller: String,
            cost: String
        ): Single<Result<Int>> =
            Single.fromCallable { AuthHelper.getKey(context) ?: "" }
                .map { apiKey ->
                    service.addPayment(
                        apiKey,
                        RequestPayment(
                            categoryId,
                            date,
                            description,
                            seller,
                            cost
                        )
                    ).execute()
                }
                .map { response ->
                    Mapper.responseToResult<Id, Int>(response) {
                        it.id
                    }
                }

        @JvmStatic
        fun editPayment(
            context: Context,
            id: Int,
            categoryId: Int,
            date: Date,
            description: String,
            seller: String,
            cost: String
        ): Single<Result<Int>> =
            Single.fromCallable { AuthHelper.getKey(context) ?: "" }
                .map { apiKey ->
                    service.editPayment(
                        apiKey,
                        id,
                        RequestPayment(
                            categoryId,
                            date,
                            description,
                            seller,
                            cost
                        )
                    ).execute()
                }
                .map { response ->
                    Mapper.responseToResult<Nothing, Nothing>(response)
                }

        @JvmStatic
        fun getPayment(context: Context, id: Int): Single<Result<Payment>> =
            Single.fromCallable { AuthHelper.getKey(context) }
                .map { apiKey ->
                    service.getPayment(apiKey, id).execute()
                }
                .map { response ->
                    Mapper.responseToResult<ApiPayment, Payment>(response) {
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

        @JvmStatic
        fun deletePayment(context: Context, id: Int): Single<Result<Nothing>> =
            Single.fromCallable { AuthHelper.getKey(context) }
                .map { apiKey ->
                    service.deletePayment(apiKey, id).execute()
                }
                .map { response ->
                    Mapper.responseToResult<Nothing, Nothing>(response)
                }
    }
}