package ru.neofusion.undead.myexpenses.ui.templates

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
import ru.neofusion.undead.myexpenses.domain.Template
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import java.lang.Exception

class EditTemplateViewModel : ViewModel() {
    val resultTemplate = MutableLiveData<Result<Template>>()
    val resultCategories = MutableLiveData<Result<List<Category>>>()

    private val compositeDisposable = CompositeDisposable()

    fun subscribe(context: Context, templateId: Int) {
        compositeDisposable.clear()
        compositeDisposable.add(
            Single.zip(
                MyExpenses.TemplateApi.getTemplate(context, templateId),
                MyExpenses.CategoryApi.getCategories(context),
                BiFunction { templateResult: Result<Template>, categoriesResult: Result<List<Category>> ->
                    templateResult to categoriesResult
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (templateResult, categoriesResult) ->
                    resultCategories.value = categoriesResult
                    resultTemplate.value = templateResult
                }, {
                    resultCategories.value =
                        Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                    resultTemplate.value =
                        Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}