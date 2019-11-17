package ru.neofusion.undead.myexpenses

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import ru.neofusion.undead.myexpenses.repository.storage.AuthHelper

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (AuthHelper.isLogined(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        initViews()
    }

    private fun initViews() {
        loginButton.setOnClickListener {
            // TODO make call and go to main activity
        }
    }
}