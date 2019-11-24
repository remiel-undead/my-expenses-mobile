package ru.neofusion.undead.myexpenses.ui.categories

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.ui.BaseListViewModelFragment
import ru.neofusion.undead.myexpenses.ui.ResultViewModel

class CategoriesFragment : BaseListViewModelFragment<Category>() {

    private lateinit var categoriesAdapter: CategoriesAdapter

    override val viewModel: ResultViewModel<List<Category>>
        get() = ViewModelProviders.of(this).get(CategoriesViewModel::class.java)

    override fun doOnResult(result: Result<List<Category>>) {
        if (result is Result.Success) {
            categoriesAdapter.setCategories(result.value)
            emptyListTextView.visibility = if (result.value.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriesAdapter = CategoriesAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = categoriesAdapter
    }
}