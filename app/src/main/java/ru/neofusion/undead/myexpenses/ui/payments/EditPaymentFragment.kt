package ru.neofusion.undead.myexpenses.ui.payments

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_payment.*
import ru.neofusion.undead.myexpenses.DateUtils.formatToString
import ru.neofusion.undead.myexpenses.DateUtils.formatToDate
import ru.neofusion.undead.myexpenses.PaymentActivity
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.ui.RoublesTextWatcher
import ru.neofusion.undead.myexpenses.ui.UiHelper
import java.util.*

class EditPaymentFragment(
    private val paymentId: Int
) : Fragment() {

    companion object {
        fun newInstance(
            paymentId: Int
        ): EditPaymentFragment =
            EditPaymentFragment(paymentId)
    }

    private lateinit var categories: List<Category>

    private val compositeDisposable = CompositeDisposable()

    private lateinit var viewModel: EditPaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_payment, container, false)
        retainInstance = true
        viewModel = ViewModelProviders.of(this).get(EditPaymentViewModel::class.java)
        viewModel.resultCategories.observe(this, androidx.lifecycle.Observer {
            doOnCategoriesResult(it)
        })
        viewModel.resultPayment.observe(this, androidx.lifecycle.Observer {
            doOnPaymentResult(it)
        })
        return view
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }

    private fun doOnPaymentResult(result: Result<Payment>) {
        if (result is Result.Success) {
            val payment = result.value
            initControls(payment)
        } else {
            UiHelper.snack(requireActivity(), (result as Result.Error).message)
        }
    }

    private fun doOnCategoriesResult(result: Result<List<Category>>) {
        if (result is Result.Success) {
            categories = result.value
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                result.value.map { it.name }
            )
            spinnerCategory.adapter = adapter
            if (result.value.isNotEmpty()) {
                spinnerCategory.setSelection(0)
            } else {
                UiHelper.snack(requireActivity(), getString(R.string.error_no_categories))
                requireActivity().finish()
            }
            adapter.notifyDataSetChanged()
        } else {
            UiHelper.snack(requireActivity(), (result as Result.Error).message)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        datePicker.setText(Date().formatToString())
        datePicker.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    datePicker.setText(calendar.time.formatToString())
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        saveButton.setOnClickListener {
            savePayment { paymentId ->
                UiHelper.snack(requireActivity(), "Отредактирован платеж $paymentId")
                finishWithSuccess(paymentId)
            }
        }
        redoButton.setOnClickListener {
            // TODO redo from edit
        }
        deleteButton.setOnClickListener {
            // TODO delete
        }

        etCost.addTextChangedListener(RoublesTextWatcher(etCost))

        viewModel.subscribe(requireContext(), paymentId)
    }

    private fun initControls(payment: Payment) {
        spinnerCategory.adapter.count.takeIf { it > 0 }.let {
            val index = categories.indexOfFirst { it.id == payment.categoryId }
            spinnerCategory.setSelection(if (index != -1) index else 0)
        }
        datePicker.setText(payment.date.formatToString())
        etDescription.setText(payment.description ?: "")
        etSeller.setText(payment.seller ?: "")
        etCost.setText(payment.cost)
    }

    private fun savePayment(doOnSuccess: (Int) -> Unit) {
        if (!areFieldsValid()) {
            return
        }

        compositeDisposable.add(
            MyExpenses.PaymentApi.editPayment(
                requireContext(),
                paymentId,
                categories[spinnerCategory.selectedItemPosition].id,
                datePicker.text.toString().formatToDate() ?: Date(),
                etDescription.text.toString(),
                etSeller.text.toString(),
                etCost.text.toString()
            )
                .doOnSubscribe {
                    requireActivity().runOnUiThread {
                        saveButton.isEnabled = false
                        redoButton.isEnabled = false
                        deleteButton.isEnabled = false
                    }
                }
                .doOnTerminate {
                    requireActivity().runOnUiThread {
                        saveButton.isEnabled = true
                        redoButton.isEnabled = true
                        deleteButton.isEnabled = true
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (result is Result.Success) {
                        doOnSuccess.invoke(paymentId)
                    } else {
                        UiHelper.snack(requireActivity(), (result as Result.Error).message)
                    }
                }, {
                    UiHelper.snack(requireActivity(), it.message ?: "Ой-ой-ой")
                })
        )
    }

    private fun finishWithSuccess(paymentId: Int) {
        requireActivity().setResult(
            RESULT_OK,
            Intent().apply { PaymentActivity.putPaymentId(this, paymentId) })
        requireActivity().finish()
    }

    private fun areFieldsValid(): Boolean {
        var result = true
        datePicker.text.takeIf { it.isNullOrEmpty() }?.let {
            datePicker.error = getString(R.string.error_empty)
            result = false
        }
        etCost.text.takeIf { it.isNullOrEmpty() }?.let {
            etCost.error = getString(R.string.error_empty)
            result = false
        }
        return result
    }
}