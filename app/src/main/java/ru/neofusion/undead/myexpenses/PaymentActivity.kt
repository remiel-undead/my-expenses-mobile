package ru.neofusion.undead.myexpenses

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.neofusion.undead.myexpenses.ui.payments.AddPaymentFragment

class PaymentActivity : AppCompatActivity() {
    companion object {

        private const val KEY_PAYMENT_ID = "paymentId"

        fun getPaymentId(bundle: Bundle?) = bundle?.getInt(KEY_PAYMENT_ID)

        fun putPaymentId(intent: Intent, paymentId: Int) {
            intent.extras?.putInt(KEY_PAYMENT_ID, paymentId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.paymentFragment,
                AddPaymentFragment.newInstance()
            )
            commit()
        }
    }
}