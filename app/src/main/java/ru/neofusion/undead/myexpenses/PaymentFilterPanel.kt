package ru.neofusion.undead.myexpenses

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import ru.neofusion.undead.myexpenses.DateUtils.formatToString
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.FilterPanelSettings
import ru.neofusion.undead.myexpenses.domain.Period
import ru.neofusion.undead.myexpenses.repository.storage.FilterPanelSettingsStorage
import java.util.*

class PaymentFilterPanel(private val slidingUpPanelLayout: SlidingUpPanelLayout) {
    private val categories = mutableListOf<Category>()

    private val datePickerStart = slidingUpPanelLayout.findViewById<EditText>(R.id.datePickerStart)
    private val datePickerEnd = slidingUpPanelLayout.findViewById<EditText>(R.id.datePickerEnd)
    private val spinnerCategory = slidingUpPanelLayout.findViewById<Spinner>(R.id.spinnerCategory)
    val findButton = slidingUpPanelLayout.findViewById<Button>(R.id.findButton)
    private val spinnerPeriod = slidingUpPanelLayout.findViewById<Spinner>(R.id.spinnerPeriod)

    init {
        spinnerCategory.adapter = ArrayAdapter(
            slidingUpPanelLayout.context,
            android.R.layout.simple_list_item_1,
            categories.map { it.name }
        )
        spinnerPeriod.adapter = ArrayAdapter(
            slidingUpPanelLayout.context,
            android.R.layout.simple_list_item_1,
            Period.values().map { it.name }
        )
        spinnerPeriod.onItemSelectedListener
        FilterPanelSettingsStorage.getSettings(slidingUpPanelLayout.context).let { settings ->
            datePickerStart.setText(settings.dateStart ?: Date().formatToString())
            datePickerEnd.setText(settings.dateEnd ?: Date().formatToString())
            spinnerCategory.setSelection(settings.category?.let { id -> categories.indexOfFirst { it.id == id } }
                ?.takeIf { it != -1 } ?: 0)
            spinnerPeriod.setSelection(settings.period?.ordinal ?: 0)
        }
        findButton.isEnabled = false
    }

    fun updateCategories(newCategories: List<Category>) {
        categories.clear()
        categories.addAll(newCategories)
        spinnerCategory.adapter
    }

    fun getSettings() = FilterPanelSettings(
        datePickerStart.text.toString(),
        datePickerEnd.text.toString(),
        spinnerCategory.selectedItemPosition.takeIf { it != -1 }?.let { categories[it].id },
        true, // TODO
        Period.values()[spinnerPeriod.selectedItemPosition]
    )

    var panelState: SlidingUpPanelLayout.PanelState = slidingUpPanelLayout.panelState
        set(value) {
            slidingUpPanelLayout.panelState = value
            field = value
        }
}