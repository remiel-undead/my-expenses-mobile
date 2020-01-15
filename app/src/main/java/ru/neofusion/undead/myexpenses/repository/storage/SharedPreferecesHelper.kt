package ru.neofusion.undead.myexpenses.repository.storage

import android.content.Context
import android.content.SharedPreferences

fun Context.getSharedPreferences(): SharedPreferences? =
    this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)