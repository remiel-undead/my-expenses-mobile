package ru.neofusion.undead.myexpenses.ui

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar

object UiHelper {
    fun snack(activity: Activity, message: String) {
        Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        view?.let { v ->
            (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
}