package ru.neofusion.undead.myexpenses.ui.payments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.neofusion.undead.myexpenses.PaymentActivity
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.ui.BaseListViewModelFragment
import ru.neofusion.undead.myexpenses.ui.ResultViewModel

class PaymentsFragment : BaseListViewModelFragment<Payment>() {
    private lateinit var paymentsAdapter: PaymentsAdapter

    override val viewModel: ResultViewModel<List<Payment>>
        get() = ViewModelProviders.of(this).get(PaymentsViewModel::class.java)

    override fun doOnResult(result: Result<List<Payment>>) {
        if (result is Result.Success) {
            paymentsAdapter.setPayments(result.value)
            emptyListTextView.visibility = if (result.value.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentsAdapter = PaymentsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = paymentsAdapter

        addButton.setOnClickListener {
            startActivity(Intent(activity, PaymentActivity::class.java))
        }
    }
}