package ru.neofusion.undead.myexpenses.ui.payments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_payments.*
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.Api
import ru.neofusion.undead.myexpenses.ui.BaseViewModelFragment
import ru.neofusion.undead.myexpenses.ui.ResultViewModel
import ru.neofusion.undead.myexpenses.ui.UiHelper
import java.math.BigDecimal
import java.util.*

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

        addButton.setOnClickListener {
            // TODO move to create payment fragment
            /*Api.getCategories(requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .flatMap { result ->
                    if (result is Result.Success) {
                        Api.addPayment(
                            requireContext(),
                            result.value.first().id,
                            Date(),
                            "Описание 0",
                            "Продавец",
                            BigDecimal("123.00")
                        )
                    } else {
                        Single.error((result as Result.Error).cause)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ result ->
                    if (result is Result.Success) {
                        UiHelper.snack(activity as Activity, "Добавлен платеж ${result.value}")
                    } else {
                        UiHelper.snack(activity as Activity, (result as Result.Error).message)
                    }
                }, {
                    UiHelper.snack(activity as Activity, it.message ?: "Ой-ой-ой")
                })*/
        }
    }
}