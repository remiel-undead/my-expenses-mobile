package ru.neofusion.undead.myexpenses

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.Api
import ru.neofusion.undead.myexpenses.repository.storage.AuthHelper
import ru.neofusion.undead.myexpenses.ui.UiHelper

class LoginActivity : AppCompatActivity() {
    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (AuthHelper.isLogined(this)) {
            goToMainActivityAndFinish()
        }
        initViews()
    }

    private fun goToMainActivityAndFinish() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun initViews() {
        loginButton.setOnClickListener {
            UiHelper.hideKeyboard(this)
            login()
        }
    }

    private fun login() {
        (loginEditText.text to passwordEditText.text).takeUnless { (login, password) ->
            login.isNullOrEmpty() || password.isNullOrEmpty()
        }?.let { (login, password) ->
            compositeDisposable.add(
                Api.login(login.toString(), password.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .map { result ->
                        if (result is Result.Success) {
                            AuthHelper.login(this, result.value)
                        }
                        result
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result: Result<String?> ->
                        if (result is Result.Success) {
                            goToMainActivityAndFinish()
                        } else {
                            val errorMessage = (result as Result.Error).message
                            UiHelper.snack(this, errorMessage)
                            Log.e(TAG, errorMessage)
                        }
                    }, { t: Throwable? ->
                        val errorMessage = t?.message ?: getString(R.string.login_error)
                        UiHelper.snack(this, errorMessage)
                        Log.e(TAG, errorMessage)
                    })
            )
        } ?: run {
            loginEditText.takeIf { it.text.isNullOrEmpty() }?.error =
                getString(R.string.error_empty)
            passwordEditText.takeIf { it.text.isNullOrEmpty() }?.error =
                getString(R.string.error_empty)
        }
    }
}