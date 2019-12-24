package ru.neofusion.undead.myexpenses

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.domain.UnauthorizedException
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.repository.storage.AuthHelper
import ru.neofusion.undead.myexpenses.ui.UiHelper

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_payments, R.id.navigation_templates, R.id.navigation_categories
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.navigation_filter_payments)?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_logout -> {
                compositeDisposable.add(
                    MyExpenses.AuthApi.logout(this)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .map { result ->
                            if (result is Result.Success
                                || result is Result.Error && result.cause is UnauthorizedException
                            ) {
                                AuthHelper.logout(this)
                            }
                            result
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result: Result<Nothing?> ->
                            if (result is Result.Success) {
                                goToLoginActivityAndFinish()
                            } else {
                                result as Result.Error
                                val errorMessage = result.message
                                UiHelper.snack(this, errorMessage)
                                Log.e(TAG, errorMessage)
                                if (result.cause is UnauthorizedException) {
                                    goToLoginActivityAndFinish()
                                }
                            }
                        }, { t: Throwable? ->
                            val errorMessage = t?.message ?: getString(R.string.login_error)
                            UiHelper.snack(this, errorMessage)
                            Log.e(TAG, errorMessage)
                        })
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToLoginActivityAndFinish() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
