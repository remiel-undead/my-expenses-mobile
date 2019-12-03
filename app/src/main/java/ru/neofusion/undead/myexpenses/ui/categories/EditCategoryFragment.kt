package ru.neofusion.undead.myexpenses.ui.categories

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_category.*
import ru.neofusion.undead.myexpenses.CategoryActivity
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.ui.UiHelper

class EditCategoryFragment(
    private val categoryId: Int
) : Fragment() {

    companion object {
        fun newInstance(categoryId: Int): EditCategoryFragment =
            EditCategoryFragment(categoryId)
    }

    private lateinit var categories: List<Category>

    private val compositeDisposable = CompositeDisposable()

    private lateinit var viewModel: EditCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_category, container, false)
        retainInstance = true
        viewModel = ViewModelProviders.of(this).get(EditCategoryViewModel::class.java)
        viewModel.resultAllCategories.observe(this, androidx.lifecycle.Observer {
            doOnCategoriesResult(it)
        })
        viewModel.resultCategory.observe(this, androidx.lifecycle.Observer {
            doOnCategoryResult(it)
        })
        return view
    }

    private fun doOnCategoryResult(result: Result<Category>) {
        if (result is Result.Success) {
            val category = result.value
            initControls(category)
        } else {
            UiHelper.snack(requireActivity(), (result as Result.Error).message)
        }
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }

    private fun doOnCategoriesResult(result: Result<List<Category>>) {
        if (result is Result.Success) {
            categories = result.value
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                arrayListOf(getString(R.string.no_parent_category))
                    .apply {
                        addAll(result.value.map { it.name })
                    }
            )
            spinnerParentCategory.adapter = adapter
            if (result.value.isNotEmpty()) {
                spinnerParentCategory.setSelection(0)
            } else {
                UiHelper.snack(requireActivity(), getString(R.string.error_no_categories))
                requireActivity().finish()
            }
            adapter.notifyDataSetChanged()
        } else {
            UiHelper.snack(requireActivity(), (result as Result.Error).message)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton.setOnClickListener {
            saveCategory { categoryId ->
                finishWithSuccess(categoryId)
            }
        }

        viewModel.subscribe(requireContext(), categoryId)
    }

    private fun initControls(category: Category) {
        spinnerParentCategory.adapter.count.takeIf { it > 0 }.let {
            val index = categories.indexOfFirst { it.id == category.parentId }
            spinnerParentCategory.setSelection(if (index != -1) index + 1 else 0)
        }
        etName.setText(category.name)
        checkboxHidden.isChecked = category.hidden
    }

    private fun saveCategory(doOnSuccess: (Int) -> Unit) {
        if (!areFieldsValid()) {
            return
        }

        compositeDisposable.add(
            MyExpenses.CategoryApi.editCategory(
                requireContext(),
                categoryId,
                etName.text.toString(),
                spinnerParentCategory.selectedItemPosition.takeIf { it != 0 }
                    ?.let { categories[spinnerParentCategory.selectedItemPosition - 1].id },
                checkboxHidden.isChecked
            )
                .doOnSubscribe {
                    requireActivity().runOnUiThread {
                        saveButton.isEnabled = false
                    }
                }
                .doOnTerminate {
                    requireActivity().runOnUiThread {
                        saveButton.isEnabled = true
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (result is Result.Success) {
                        doOnSuccess.invoke(categoryId)
                    } else {
                        UiHelper.snack(requireActivity(), (result as Result.Error).message)
                    }
                }, {
                    UiHelper.snack(requireActivity(), it.message ?: "Ой-ой-ой")
                })
        )
    }

    private fun finishWithSuccess(categoryId: Int) {
        requireActivity().setResult(
            RESULT_OK,
            Intent().apply { CategoryActivity.putCategoryId(this, categoryId) })
        requireActivity().finish()
    }

    private fun areFieldsValid(): Boolean {
        var result = true
        etName.text.takeIf { it.isNullOrEmpty() }?.let {
            etName.error = getString(R.string.error_empty)
            result = false
        }
        return result
    }
}