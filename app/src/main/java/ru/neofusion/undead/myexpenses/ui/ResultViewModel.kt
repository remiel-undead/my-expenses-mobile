package ru.neofusion.undead.myexpenses.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.neofusion.undead.myexpenses.domain.Result
import java.lang.Exception

abstract class ResultViewModel<T : Any?> : ViewModel() {
    val result = MutableLiveData<Result<T>>()
    private val compositeDisposable = CompositeDisposable()

    fun subscribe(
        context: Context,
        doOnSubscribe: (() -> Unit)? = null,
        doOnTerminate: (() -> Unit)? = null
    ) {
        compositeDisposable.clear()
        compositeDisposable.add(
            loadData(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { doOnSubscribe?.invoke() }
                .doOnTerminate { doOnTerminate?.invoke() }
                .subscribe({
                    result.value = it
                }, {
                    result.value = Result.Error(it.message ?: "Ой-ой-ой", Exception(it.message))
                })

        )
    }

    abstract fun loadData(context: Context): Single<Result<T>>

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}