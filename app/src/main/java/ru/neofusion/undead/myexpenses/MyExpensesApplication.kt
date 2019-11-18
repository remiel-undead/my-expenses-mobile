package ru.neofusion.undead.myexpenses

import android.app.Application
import ru.neofusion.undead.myexpenses.repository.network.Api

class MyExpensesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Api.init(this)
    }
}