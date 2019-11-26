package ru.neofusion.undead.myexpenses.ui.payments

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import java.lang.Exception

class EditPaymentViewModel : ViewModel() {
    val resultPayment = MutableLiveData<Result<Payment>>()
    val resultCategories = MutableLiveData<Result<List<Category>>>()

    private val compositeDisposable = CompositeDisposable()

    fun subscribe(context: Context, paymentId: Int) {
        compositeDisposable.clear()
        compositeDisposable.add(
            Single.zip(
                MyExpenses.PaymentApi.getPayment(context, paymentId),
                MyExpenses.CategoryApi.getCategories(context),
                BiFunction { paymentResult: Result<Payment>, categoriesResult: Result<List<Category>> ->
                    paymentResult to categoriesResult
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (paymentResult, categoriesResult) ->
                    resultCategories.value = categoriesResult
                    resultPayment.value = paymentResult
                }, {
                    resultCategories.value =
                        Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                    resultPayment.value =
                        Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}