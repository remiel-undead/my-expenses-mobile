package ru.neofusion.undead.myexpenses.ui.categories

import android.content.Context
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.ui.ResultViewModel

class CategoriesViewModel : ResultViewModel<List<Category>>() {
    override fun loadData(context: Context) =
        MyExpenses.CategoryApi.getCategories(context)
}