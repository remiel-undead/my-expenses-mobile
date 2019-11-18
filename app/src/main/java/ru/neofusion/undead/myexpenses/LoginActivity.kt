package ru.neofusion.undead.myexpenses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import ru.neofusion.undead.myexpenses.repository.Result
import ru.neofusion.undead.myexpenses.repository.network.Api
import ru.neofusion.undead.myexpenses.repository.storage.AuthHelper

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
            hideKeyboard()
            login()
        }
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    private fun login() {
        (loginEditText.text to passwordEditText.text).takeUnless { (login, password) ->
            login.isNullOrEmpty() || password.isNullOrEmpty()
        }?.let { (login, password) ->
            compositeDisposable.add(
                Api.login(login.toString(), password.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .doOnSubscribe {
                        // TODO show progress
                    }
                    .doOnTerminate {
                        // TODO hide progress
                    }
                    .subscribe({ result: Result<String> ->
                        if (result is Result.Success) {
                            AuthHelper.login(this, result.value)
                            goToMainActivityAndFinish()
                        } else {
                            val errorMessage = (result as Result.Error).message
                            snack(errorMessage)
                            Log.e(TAG, errorMessage)
                        }
                    }, { t: Throwable? ->
                        val errorMessage = t?.message ?: getString(R.string.login_error)
                        snack(errorMessage)
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

    private fun snack(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
}