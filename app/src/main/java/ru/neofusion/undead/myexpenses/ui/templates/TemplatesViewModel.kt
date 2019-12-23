package ru.neofusion.undead.myexpenses.ui.templates

import android.content.Context
import ru.neofusion.undead.myexpenses.domain.Template
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.ui.ResultViewModel

class TemplatesViewModel : ResultViewModel<List<Template>>() {
    override fun loadData(context: Context) =
        MyExpenses.TemplateApi.getTemplates(context)
}