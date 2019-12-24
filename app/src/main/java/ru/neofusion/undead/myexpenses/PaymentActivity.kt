package ru.neofusion.undead.myexpenses

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.neofusion.undead.myexpenses.ui.payments.AddPaymentFragment
import ru.neofusion.undead.myexpenses.ui.payments.EditPaymentFragment

class PaymentActivity : AppCompatActivity() {
    companion object {

        private const val KEY_PAYMENT_ID = "paymentId"
        private const val KEY_CATEGORY_ID = "categoryId"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_SELLER = "seller"
        private const val KEY_COST_STRING = "costString"

        private const val KEY_OPERATION = "operation"

        fun getPaymentId(bundle: Bundle?) = bundle?.getInt(KEY_PAYMENT_ID, -1) ?: -1

        fun getOperation(bundle: Bundle?) = bundle?.getString(KEY_OPERATION)

        fun getPaymentCategoryId(bundle: Bundle?) = bundle?.getInt(KEY_CATEGORY_ID, -1) ?: -1

        fun getDescription(bundle: Bundle?) = bundle?.getString(KEY_DESCRIPTION)

        fun getSeller(bundle: Bundle?) = bundle?.getString(KEY_SELLER)

        fun getCostString(bundle: Bundle?) = bundle?.getString(KEY_COST_STRING)

        fun putPaymentId(intent: Intent, paymentId: Int) {
            intent.putExtra(KEY_PAYMENT_ID, paymentId)
        }

        fun putCategoryId(intent: Intent, categoryId: Int?) {
            categoryId?.let { intent.putExtra(KEY_CATEGORY_ID, it) }
        }

        fun putDescription(intent: Intent, description: String?) {
            intent.putExtra(KEY_DESCRIPTION, description)
        }

        fun putSeller(intent: Intent, seller: String?) {
            intent.putExtra(KEY_SELLER, seller)
        }

        fun putCostString(intent: Intent, costString: String?) {
            intent.putExtra(KEY_COST_STRING, costString)
        }

        fun putOperation(intent: Intent, operation: Operation) {
            intent.putExtra(KEY_OPERATION, operation.name)
        }
    }

    enum class Operation {
        ADD, EDIT, REDO, DELETE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        val bundle = intent.extras
        val paymentId = getPaymentId(bundle)
        supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.paymentFragment,
                if (paymentId != -1)
                    EditPaymentFragment.newInstance(paymentId)
                else
                    AddPaymentFragment.newInstance(
                        getPaymentCategoryId(bundle).takeIf { it != -1 },
                        getDescription(bundle),
                        getSeller(bundle),
                        getCostString(bundle)
                    )
            )
            commit()
        }
    }
}