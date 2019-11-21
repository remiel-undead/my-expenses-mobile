package ru.neofusion.undead.myexpenses.ui.payments

import android.content.Context
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.repository.network.Api
import ru.neofusion.undead.myexpenses.repository.network.result.Order
import ru.neofusion.undead.myexpenses.ui.ResultViewModel
import ru.neofusion.undead.myexpenses.DateUtils.plus
import java.util.*

class PaymentsViewModel : ResultViewModel<List<Payment>>() {
    private var startDate: Date = Date().plus(Calendar.MONTH, -1)
    private var endDate: Date = Date()
    private var order: Order = Order.BY_DATE_ASC
    private var categoryId: Int? = null
    private var useSubCategories: Boolean = true

    override fun loadData(context: Context) =
        Api.getPayments(
            context,
            startDate,
            endDate,
            order,
            categoryId,
            useSubCategories
        )
}