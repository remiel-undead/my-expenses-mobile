package ru.neofusion.undead.myexpenses.ui.categories

import android.app.Activity
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
import ru.neofusion.undead.myexpenses.ui.UiHelper

class CategoriesFragment : BaseListViewModelFragment<Category>() {
    companion object {
        private const val REQUEST_CODE_EDIT_CATEGORY = 10000
        private const val REQUEST_CODE_ADD_CATEGORY = 10001
    }

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
                            startActivityForResult(intent, REQUEST_CODE_EDIT_CATEGORY)
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
            startActivityForResult(
                Intent(activity, CategoryActivity::class.java),
                REQUEST_CODE_ADD_CATEGORY
            )
        }
        longClickOptions = arrayOf(
            getString(R.string.long_tap_option_edit),
            getString(R.string.long_tap_option_add_payment_to_category),
            getString(R.string.long_tap_option_find_payments_by_category)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val categoryId = CategoryActivity.getCategoryId(data?.extras)
            if (categoryId != -1) {
                when (requestCode) {
                    REQUEST_CODE_ADD_CATEGORY -> {
                        UiHelper.snack(requireActivity(), "Добавлена категория $categoryId")
                    }
                    REQUEST_CODE_EDIT_CATEGORY -> {
                        UiHelper.snack(requireActivity(), "Отредактирована категория $categoryId")
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}