package ru.neofusion.undead.myexpenses.ui.categories

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.neofusion.undead.myexpenses.CategoryActivity
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.ui.BaseListViewModelFragment
import ru.neofusion.undead.myexpenses.ui.ResultViewModel

class CategoriesFragment : BaseListViewModelFragment<Category>() {
    interface CategoryLongClickListener {
        fun onCategorytLongClick(category: Category)
    }

    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var longClickOptions: Array<String>

    private val onCategoryLongClickListener = object : CategoryLongClickListener {
        override fun onCategorytLongClick(category: Category) {
            val dialog = AlertDialog.Builder(requireContext())
                .setItems(longClickOptions) { _, which ->
                    when (which) {
                        0 -> { // edit
                            val intent = Intent(activity, CategoryActivity::class.java)
                            CategoryActivity.putCategoryId(intent, category.id)
                            startActivity(intent)
                        }
                        1 -> { // add payment to cat

                        }
                        2 -> { // find payments by cat

                        }
                    }
                }.create()
            dialog.show()
        }

    }

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

        categoriesAdapter = CategoriesAdapter(onCategoryLongClickListener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = categoriesAdapter

        addButton.setOnClickListener {
            startActivity(Intent(activity, CategoryActivity::class.java))
        }
        longClickOptions = arrayOf(
            getString(R.string.long_tap_option_edit),
            getString(R.string.long_tap_option_add_payment_to_category),
            getString(R.string.long_tap_option_find_payments_by_category)
        )
    }
}