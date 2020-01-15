package ru.neofusion.undead.myexpenses.ui.payments

import android.content.Context
import io.reactivex.Single
import ru.neofusion.undead.myexpenses.DateUtils.formatToDate
import ru.neofusion.undead.myexpenses.PeriodUtils.getDates
import ru.neofusion.undead.myexpenses.domain.FilterPanelSettings
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.domain.Payment
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.repository.network.result.Order
import ru.neofusion.undead.myexpenses.ui.ResultViewModel
import java.util.*

class PaymentsViewModel : ResultViewModel<List<Payment>>() {
    private var filterPanelSettings: FilterPanelSettings? = null

    fun setFilterPanelSettings(filterPanelSettings: FilterPanelSettings) {
        this.filterPanelSettings = filterPanelSettings
    }

    override fun loadData(context: Context): Single<Result<List<Payment>>> {
        val order: Order = Order.BY_DATE_ASC // TODO

        val period = filterPanelSettings?.period?.getDates()
        val dateStart = period?.first
            ?: filterPanelSettings?.dateStart?.formatToDate() ?: Date()
        val dateEnd = period?.second
            ?: filterPanelSettings?.dateEnd?.formatToDate() ?: Date()
        return MyExpenses.PaymentApi.getPayments(
            context,
            dateStart,
            dateEnd,
            order,
            filterPanelSettings?.category,
            filterPanelSettings?.useSubcategories ?: true
        )
    }

}