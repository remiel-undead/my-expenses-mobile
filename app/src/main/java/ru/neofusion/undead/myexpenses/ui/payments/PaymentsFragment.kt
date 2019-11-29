package ru.neofusion.undead.myexpenses.ui.payments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.neofusion.undead.myexpenses.PaymentActivity
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.ui.BaseListViewModelFragment
import ru.neofusion.undead.myexpenses.ui.ResultViewModel
import ru.neofusion.undead.myexpenses.ui.UiHelper

class PaymentsFragment : BaseListViewModelFragment<Payment>() {
    interface PaymentLongClickListener {
        fun onPaymentLongClick(payment: Payment)
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var paymentsAdapter: PaymentsAdapter
    private lateinit var longClickOptions: Array<String>

    private val paymentLongClickListener = object : PaymentLongClickListener {
        override fun onPaymentLongClick(payment: Payment) {
            val dialog = AlertDialog.Builder(requireContext())
                .setItems(longClickOptions) { _, which ->
                    when (which) {
                        0 -> { // edit
                            val intent = Intent(activity, PaymentActivity::class.java)
                            PaymentActivity.putPaymentId(intent, payment.id)
                            startActivity(intent)
                        }
                        1 -> {

                        }
                        2 -> { // redo
                            val intent = Intent(activity, PaymentActivity::class.java)
                            PaymentActivity.putCategoryId(intent, payment.categoryId)
                            PaymentActivity.putDescription(intent, payment.description)
                            PaymentActivity.putSeller(intent, payment.seller)
                            PaymentActivity.putCostString(
                                intent,
                                payment.cost
                            )
                            startActivity(intent)
                        }
                        3 -> { // delete
                            showDeletePaymentDialog(payment.id)
                        }
                    }
                }.create()
            dialog.show()
        }
    }

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

        paymentsAdapter = PaymentsAdapter(paymentLongClickListener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = paymentsAdapter

        addButton.setOnClickListener {
            startActivity(Intent(activity, PaymentActivity::class.java))
        }
        longClickOptions = arrayOf(
            getString(R.string.long_tap_option_edit),
            getString(R.string.long_tap_option_add_as_template),
            getString(R.string.long_tap_option_redo),
            getString(R.string.long_tap_option_delete)
        )
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }

    private fun showDeletePaymentDialog(paymentId: Int) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_payment_dialog_message)
            .setPositiveButton(R.string.button_text_delete) { dialog, _ ->
                compositeDisposable.add(
                    MyExpenses.PaymentApi.deletePayment(requireContext(), paymentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            if (result is Result.Success) {
                                UiHelper.snack(requireActivity(), "Платеж $paymentId удален")
                            } else {
                                UiHelper.snack(requireActivity(), (result as Result.Error).message)
                            }
                        }, {
                            UiHelper.snack(requireActivity(), it.message ?: "Ой-ой-ой")
                        })
                )
                dialog.dismiss()
            }
            .setNegativeButton(R.string.delete_payment_dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }
}