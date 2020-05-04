package ru.neofusion.undead.myexpenses.ui.payments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.neofusion.undead.myexpenses.PaymentActivity
import ru.neofusion.undead.myexpenses.PaymentFilterPanel
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.TemplateActivity
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.ui.UiHelper

class PaymentsFragment : Fragment() {
    companion object {
        private const val REQUEST_CODE_EDIT_PAYMENT = 1000
        private const val REQUEST_CODE_ADD_PAYMENT = 1001
        private const val REQUEST_CODE_ADD_AS_TEMPLATE = 1002
    }

    interface PaymentLongClickListener {
        fun onPaymentLongClick(payment: Payment)
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var paymentsAdapter: PaymentsAdapter
    private lateinit var longClickOptions: Array<String>

    private lateinit var paymentsViewModel: PaymentsViewModel
    private lateinit var filterPanel: PaymentFilterPanel

    private val paymentLongClickListener = object : PaymentLongClickListener {
        override fun onPaymentLongClick(payment: Payment) {
            val dialog = AlertDialog.Builder(requireContext())
                .setItems(longClickOptions) { _, which ->
                    when (which) {
                        0 -> { // edit
                            val intent = Intent(activity, PaymentActivity::class.java)
                            PaymentActivity.putPaymentId(intent, payment.id)
                            startActivityForResult(intent, REQUEST_CODE_EDIT_PAYMENT)
                        }
                        1 -> { // add as template
                            val intent = Intent(activity, TemplateActivity::class.java)
                            TemplateActivity.putCategoryId(intent, payment.category.id)
                            TemplateActivity.putDescription(intent, payment.description)
                            TemplateActivity.putSeller(intent, payment.seller)
                            TemplateActivity.putCostString(intent, payment.cost)
                            startActivityForResult(intent, REQUEST_CODE_ADD_AS_TEMPLATE)
                        }
                        2 -> { // redo
                            val intent = Intent(activity, PaymentActivity::class.java)
                            PaymentActivity.putCategoryId(intent, payment.category.id)
                            PaymentActivity.putDescription(intent, payment.description)
                            PaymentActivity.putSeller(intent, payment.seller)
                            PaymentActivity.putCostString(
                                intent,
                                payment.cost
                            )
                            startActivityForResult(intent, REQUEST_CODE_ADD_PAYMENT)
                        }
                        3 -> { // delete
                            showDeletePaymentDialog(payment.id)
                        }
                    }
                }.create()
            dialog.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_base_list, container, false)
        retainInstance = true
        paymentsViewModel = ViewModelProviders.of(this).get(PaymentsViewModel::class.java)
        paymentsViewModel.resultPayments.observe(this, Observer {
            doOnPaymentsResult(it)
        })
        paymentsViewModel.resultCategories.observe(this, Observer {
            doOnCategoriesResult(it)
        })
        return view
    }

    private fun doOnCategoriesResult(result: Result<List<Category>>) {
        if (result is Result.Success) {
            filterPanel.updateCategories(result.value)
            loadPaymentsViewData()
        } else{
            UiHelper.snack(requireActivity(), (result as Result.Error).message)
        }
    }

    private fun doOnPaymentsResult(result: Result<List<Payment>>) {
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
            startActivityForResult(
                Intent(activity, PaymentActivity::class.java),
                REQUEST_CODE_ADD_PAYMENT
            )
        }
        longClickOptions = arrayOf(
            getString(R.string.long_tap_option_edit),
            getString(R.string.long_tap_option_add_as_template),
            getString(R.string.long_tap_option_redo),
            getString(R.string.long_tap_option_delete)
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        filterPanel = PaymentFilterPanel(requireActivity().findViewById(R.id.slidingLayout))
        filterPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN

        filterPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

        filterPanel.findButton.setOnClickListener { loadPaymentsViewData() }

        loadCategoriesViewData()
    }

    private fun loadPaymentsViewData() {
        paymentsViewModel.subscribePayments(
            requireContext(),
            filterPanel.getSettings(),
            { swipeRefreshLayout.isRefreshing = true },
            { swipeRefreshLayout.isRefreshing = false })
    }

    private fun loadCategoriesViewData() {
        paymentsViewModel.subscribeCategories(
            requireContext(),
            { filterPanel.findButton.isEnabled = false },
            { filterPanel.findButton.isEnabled = true })
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
            .setNegativeButton(R.string.button_text_cancel) { dialog, _ ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val paymentId = PaymentActivity.getPaymentId(data?.extras)
            val operation = PaymentActivity.getOperation(data?.extras)
            if (paymentId != -1) {
                when (requestCode) {
                    REQUEST_CODE_ADD_PAYMENT -> {
                        UiHelper.snack(requireActivity(), "Добавлен платеж $paymentId")
                    }
                    REQUEST_CODE_EDIT_PAYMENT -> {
                        when (operation) {
                            PaymentActivity.Operation.REDO.name -> {
                                UiHelper.snack(requireActivity(), "Добавлен платеж $paymentId")
                            }
                            PaymentActivity.Operation.EDIT.name -> {
                                UiHelper.snack(
                                    requireActivity(),
                                    "Отредактирован платеж $paymentId"
                                )
                            }
                            PaymentActivity.Operation.DELETE.name -> {
                                UiHelper.snack(requireActivity(), "Удален платеж $paymentId")
                            }
                        }
                    }
                }
            }
            val templateId = TemplateActivity.getTemplateId(data?.extras)
            if (templateId != -1) {
                when (requestCode) {
                    REQUEST_CODE_ADD_AS_TEMPLATE -> {
                        UiHelper.snack(requireActivity(), "Добавлен шаблон $templateId")
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}