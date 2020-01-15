package ru.neofusion.undead.myexpenses

import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import ru.neofusion.undead.myexpenses.DateUtils.formatToString
import ru.neofusion.undead.myexpenses.repository.storage.FilterPanelSettingsStorage
import java.util.*

class PaymentFilterPanel(private val slidingUpPanelLayout: SlidingUpPanelLayout) {
    init {
        FilterPanelSettingsStorage.getSettings(slidingUpPanelLayout.context).let { settings ->
            datePickerStart.setText(settings.dateStart ?: Date().formatToString())
            datePickerEnd.setText(settings.dateEnd ?: Date().formatToString())
            spinnerCategory.setSelection(0)
            spinnerPeriod.setSelection(0)
        }
    }

    var panelState: SlidingUpPanelLayout.PanelState = slidingUpPanelLayout.panelState
        set(value) {
            slidingUpPanelLayout.panelState = value
            field = value
        }

    val datePickerStart = slidingUpPanelLayout.findViewById<EditText>(R.id.datePickerStart)
    val datePickerEnd = slidingUpPanelLayout.findViewById<EditText>(R.id.datePickerEnd)
    val spinnerCategory = slidingUpPanelLayout.findViewById<Spinner>(R.id.spinnerCategory)
    val findButton = slidingUpPanelLayout.findViewById<Button>(R.id.findButton)
    val spinnerPeriod = slidingUpPanelLayout.findViewById<Spinner>(R.id.spinnerPeriod)
}