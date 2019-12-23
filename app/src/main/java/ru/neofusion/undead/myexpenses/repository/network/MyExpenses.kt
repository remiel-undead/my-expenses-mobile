package ru.neofusion.undead.myexpenses.repository.network

import android.content.Context
import com.google.gson.GsonBuilder
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.neofusion.undead.myexpenses.BuildConfig
import ru.neofusion.undead.myexpenses.DateUtils.formatToString
import ru.neofusion.undead.myexpenses.domain.*
import ru.neofusion.undead.myexpenses.repository.network.result.*
import ru.neofusion.undead.myexpenses.repository.network.result.Category as ApiCategory
import ru.neofusion.undead.myexpenses.repository.network.result.Payment as ApiPayment
import ru.neofusion.undead.myexpenses.repository.network.result.Template as ApiTemplate
import ru.neofusion.undead.myexpenses.repository.network.request.Category as RequestCategory
import ru.neofusion.undead.myexpenses.repository.network.request.Payment as RequestPayment
import ru.neofusion.undead.myexpenses.repository.network.request.Template as RequestTemplate
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
            Single.fromCallable { AuthHelper.getKey(context).orEmpty() }
                .map { service.logout(it).execute() }
                .map { response ->
                    Mapper.responseToResult<Nothing, Nothing>(response)
                }
    }

    object CategoryApi {
        @JvmStatic
        fun getCategories(context: Context): Single<Result<List<Category>>> =
            Single.fromCallable { AuthHelper.getKey(context).orEmpty() }
                .map { service.getCategories(it).execute() }
                .map { response ->
                    Mapper.responseToResult<List<ApiCategory>, List<Category>>(response) { apiCategories ->
                        apiCategories.map { Mapper.mapToCategory(it) }
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
                        Mapper.mapToCategory(it)
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
            Single.fromCallable { AuthHelper.getKey(context).orEmpty() }
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
                        apiPayments.map { Mapper.mapToPayment(it) }
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
            Single.fromCallable { AuthHelper.getKey(context).orEmpty() }
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
            Single.fromCallable { AuthHelper.getKey(context).orEmpty() }
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
                        Mapper.mapToPayment(it)
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

    object TemplateApi {
        @JvmStatic
        fun getTemplates(context: Context): Single<Result<List<Template>>> =
            Single.fromCallable { AuthHelper.getKey(context).orEmpty() }
                .map { apiKey ->
                    service.getTemplates(apiKey).execute()
                }
                .map { response ->
                    Mapper.responseToResult<List<ApiTemplate>, List<Template>>(response) { apiTemplates ->
                        apiTemplates.map { Mapper.mapToTemplate(it) }
                    }
                }

        @JvmStatic
        fun addTemplate(
            context: Context,
            categoryId: Int,
            description: String,
            seller: String,
            cost: String
        ): Single<Result<Int>> =
            Single.fromCallable { AuthHelper.getKey(context).orEmpty() }
                .map { apiKey ->
                    service.addTemplate(
                        apiKey,
                        RequestTemplate(
                            categoryId,
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
        fun editTemplate(
            context: Context,
            id: Int,
            categoryId: Int,
            description: String,
            seller: String,
            cost: String
        ): Single<Result<Int>> =
            Single.fromCallable { AuthHelper.getKey(context).orEmpty() }
                .map { apiKey ->
                    service.editTemplate(
                        apiKey,
                        id,
                        RequestTemplate(
                            categoryId,
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
        fun getTemplate(context: Context, id: Int): Single<Result<Template>> =
            Single.fromCallable { AuthHelper.getKey(context) }
                .map { apiKey ->
                    service.getTemplate(apiKey, id).execute()
                }
                .map { response ->
                    Mapper.responseToResult<ApiTemplate, Template>(response) {
                        Mapper.mapToTemplate(it)
                    }
                }

        @JvmStatic
        fun deleteTemplate(context: Context, id: Int): Single<Result<Nothing>> =
            Single.fromCallable { AuthHelper.getKey(context) }
                .map { apiKey ->
                    service.deleteTemplate(apiKey, id).execute()
                }
                .map { response ->
                    Mapper.responseToResult<Nothing, Nothing>(response)
                }
    }
}