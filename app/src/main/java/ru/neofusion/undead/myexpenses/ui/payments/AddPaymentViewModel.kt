package ru.neofusion.undead.myexpenses.ui.payments

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import java.lang.Exception

class AddPaymentViewModel : ViewModel() {
    val result = MutableLiveData<Result<List<Category>>>()

    private val compositeDisposable = CompositeDisposable()

    fun subscribe(context: Context) {
        compositeDisposable.clear()
        compositeDisposable.add(
            MyExpenses.CategoryApi.getCategories(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.value = it
                }, {
                    result.value =
                        Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}