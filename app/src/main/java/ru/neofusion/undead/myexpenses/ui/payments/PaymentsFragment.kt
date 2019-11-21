package ru.neofusion.undead.myexpenses.ui.payments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_payments.*
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.ui.BaseViewModelFragment
import ru.neofusion.undead.myexpenses.ui.ResultViewModel

class PaymentsFragment : BaseViewModelFragment<List<Payment>>() {
    private lateinit var paymentsAdapter: PaymentsAdapter

    override fun getViewModel(): ResultViewModel<List<Payment>> =
        ViewModelProviders.of(this).get(PaymentsViewModel::class.java)

    override fun doOnResult(result: Result<List<Payment>>) {
        if (result is Result.Success) {
            paymentsAdapter.setPayments(result.value)
            emptyListTextView.visibility = if (result.value.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun getLayoutResource(): Int = R.layout.fragment_payments

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentsAdapter = PaymentsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = paymentsAdapter
    }
}