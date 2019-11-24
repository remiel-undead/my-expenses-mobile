package ru.neofusion.undead.myexpenses.ui.payments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.databinding.ListItemPaymentBinding
import ru.neofusion.undead.myexpenses.domain.Payment

class PaymentsAdapter(private val paymentLongClickListener: PaymentsFragment.PaymentLongClickListener) :
    RecyclerView.Adapter<PaymentsAdapter.PaymentViewHolder>() {
    private var payments: List<Payment>? = null

    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): PaymentViewHolder {
        val listItemPaymentBinding: ListItemPaymentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.list_item_payment, viewGroup, false
        )
        return PaymentViewHolder(listItemPaymentBinding)
    }

    override fun onBindViewHolder(@NonNull paymentViewHolder: PaymentViewHolder, i: Int) {
        val payment = payments?.get(i)
        paymentViewHolder.listItemPaymentBinding.payment = payment
        paymentViewHolder.itemView.setOnLongClickListener {
            paymentLongClickListener.onPaymentLongClick(payment)
            true
        }
    }

    override fun getItemCount(): Int =
        payments?.size ?: 0

    fun setPayments(categories: List<Payment>) {
        this.payments = categories
        notifyDataSetChanged()
    }

    class PaymentViewHolder(val listItemPaymentBinding: ListItemPaymentBinding) :
        RecyclerView.ViewHolder(listItemPaymentBinding.root)
}