package ru.neofusion.undead.myexpenses.ui.payments

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.neofusion.undead.myexpenses.DateUtils.formatToDate
import ru.neofusion.undead.myexpenses.PeriodUtils.getDates
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.FilterPanelSettings
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.repository.network.result.Order
import java.lang.Exception
import java.util.*

class PaymentsViewModel : ViewModel() {
    val resultPayments = MutableLiveData<Result<List<Payment>>>()
    val resultCategories = MutableLiveData<Result<List<Category>>>()
    private val compositeDisposable = CompositeDisposable()

    fun subscribePayments(
        context: Context, filterPanelSettings: FilterPanelSettings,
        doOnSubscribe: (() -> Unit)? = null,
        doOnTerminate: (() -> Unit)? = null
    ) {
        compositeDisposable.add(
            loadPayments(context, filterPanelSettings)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { doOnSubscribe?.invoke() }
                .doOnTerminate { doOnTerminate?.invoke() }
                .subscribe({
                    resultPayments.value = it
                }, {
                    resultPayments.value =
                        Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                })
        )
    }

    fun subscribeCategories(
        context: Context,
        doOnSubscribe: (() -> Unit)? = null,
        doOnTerminate: (() -> Unit)? = null
    ) {
        compositeDisposable.add(
            MyExpenses.CategoryApi.getCategories(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { doOnSubscribe?.invoke() }
                .doOnTerminate { doOnTerminate?.invoke() }
                .subscribe({
                    resultCategories.value = it
                }, {
                    resultCategories.value =
                        Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                })
        )
    }

    private fun loadPayments(
        context: Context,
        filterPanelSettings: FilterPanelSettings
    ): Single<Result<List<Payment>>> {
        val order: Order = Order.BY_DATE_ASC // TODO
        val period = filterPanelSettings.period?.getDates()
        val dateStart = period?.first
            ?: filterPanelSettings.dateStart?.formatToDate() ?: Date()
        val dateEnd = period?.second
            ?: filterPanelSettings.dateEnd?.formatToDate() ?: Date()
        return MyExpenses.PaymentApi.getPayments(
            context,
            dateStart,
            dateEnd,
            order,
            filterPanelSettings.category,
            filterPanelSettings.useSubcategories ?: true
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}