package ru.neofusion.undead.myexpenses.ui.categories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import java.lang.Exception

class EditCategoryViewModel : ViewModel() {
    val resultCategory = MutableLiveData<Result<Category>>()
    val resultAllCategories = MutableLiveData<Result<List<Category>>>()

    private val compositeDisposable = CompositeDisposable()

    fun subscribe(context: Context, categoryId: Int) {
        compositeDisposable.clear()
        compositeDisposable.add(
            Single.zip(
                MyExpenses.CategoryApi.getCategory(context, categoryId),
                MyExpenses.CategoryApi.getCategories(context),
                BiFunction { categoryResult: Result<Category>, categoriesResult: Result<List<Category>> ->
                    categoryResult to categoriesResult
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (paymentResult, categoriesResult) ->
                    resultAllCategories.value = categoriesResult
                    resultCategory.value = paymentResult
                }, {
                    resultAllCategories.value =
                        Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                    resultCategory.value =
                        Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}