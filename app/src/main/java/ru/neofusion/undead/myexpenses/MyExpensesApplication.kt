package ru.neofusion.undead.myexpenses

import android.app.Application
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses

class MyExpensesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MyExpenses.init(this)
    }
}