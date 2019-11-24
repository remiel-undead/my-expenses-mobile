package ru.neofusion.undead.myexpenses.ui.payments

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_payment.*
import ru.neofusion.undead.myexpenses.BigDecimalUtils.toBigDecimalRoubles
import ru.neofusion.undead.myexpenses.DateUtils.formatToString
import ru.neofusion.undead.myexpenses.DateUtils.formatToDate
import ru.neofusion.undead.myexpenses.PaymentActivity
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.Api
import ru.neofusion.undead.myexpenses.ui.UiHelper
import java.util.*

class AddPaymentFragment : Fragment() {
    companion object {
        fun newInstance(): AddPaymentFragment = AddPaymentFragment()
    }

    private lateinit var categories: List<Category>

    private val compositeDisposable = CompositeDisposable()

    private lateinit var viewModel: AddPaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_payment, container, false)
        retainInstance = true
        viewModel = ViewModelProviders.of(this).get(AddPaymentViewModel::class.java)
        viewModel.result.observe(this, androidx.lifecycle.Observer {
            doOnCategoriesResult(it)
        })
        return view
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }

//    private fun doOnPaymentResult(result: Result<Payment?>) {
//        if (result is Result.Success) {
//            val payment = result.value
//            payment ?: return
//
//            val categoryPos = spinnerCategory.
//            spinnerCategory.setSelection()
//            datePicker.setText(payment.getDateFormatted())
//            etDescription.setText(payment.description ?: "")
//            etSeller.setText(payment.seller ?: "")
//            etCost.setText(payment.getCostToString())
//        } else {
//            UiHelper.snack(requireActivity(), (result as Result.Error).message)
//        }
//    }

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

        addButton.setOnClickListener {
            addPayment { paymentId ->
                UiHelper.snack(requireActivity(), "Добавлен платеж $paymentId")
                finishWithSuccess(paymentId)
            }
        }
        addAndCreateNewButton.setOnClickListener {
            addPayment { paymentId ->
                UiHelper.snack(requireActivity(), "Добавлен платеж $paymentId")
                clearControls()
            }
        }

        viewModel.subscribe(requireContext())
    }

    private fun clearControls() {
        spinnerCategory.adapter.count.takeIf { it > 0 }.let { spinnerCategory.setSelection(0) }
        datePicker.setText("")
        etDescription.setText("")
        etSeller.setText("")
        etCost.setText("")
    }

    private fun addPayment(doOnSuccess: (Int) -> Unit) {
        if (!areFieldsValid()) {
            return
        }

        compositeDisposable.add(
            Api.addPayment(
                requireContext(),
                categories[spinnerCategory.selectedItemPosition].id,
                datePicker.text.toString().formatToDate() ?: Date(),
                etDescription.text.toString(),
                etSeller.text.toString(),
                etCost.text.toString().toBigDecimalRoubles()
            )
                .doOnSubscribe {
                    requireActivity().runOnUiThread {
                        addButton.isEnabled = false
                        addAndCreateNewButton.isEnabled = false
                    }
                }
                .doOnTerminate {
                    requireActivity().runOnUiThread {
                        addButton.isEnabled = true
                        addAndCreateNewButton.isEnabled = true
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (result is Result.Success) {
                        doOnSuccess.invoke(result.value)
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