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
import kotlinx.android.synthetic.main.fragment_add_category.*
import ru.neofusion.undead.myexpenses.CategoryActivity
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.ui.UiHelper

class AddCategoryFragment : Fragment() {

    companion object {
        fun newInstance(): AddCategoryFragment =
            AddCategoryFragment()
    }

    private lateinit var categories: List<Category>

    private val compositeDisposable = CompositeDisposable()

    private lateinit var viewModel: AddCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_category, container, false)
        retainInstance = true
        viewModel = ViewModelProviders.of(this).get(AddCategoryViewModel::class.java)
        viewModel.result.observe(this, androidx.lifecycle.Observer {
            doOnCategoriesResult(it)
        })
        return view
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

            initControls()
        } else {
            UiHelper.snack(requireActivity(), (result as Result.Error).message)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addButton.setOnClickListener {
            addCategory { categoryId ->
                UiHelper.snack(requireActivity(), "Добавлена категория $categoryId")
                finishWithSuccess(categoryId)
            }
        }

        viewModel.subscribe(requireContext())
    }

    private fun initControls() {
        spinnerParentCategory.setSelection(0)
        etName.setText("")
        checkboxHidden.isChecked = false
    }

    private fun addCategory(doOnSuccess: (Int) -> Unit) {
        if (!areFieldsValid()) {
            return
        }

        compositeDisposable.add(
            MyExpenses.CategoryApi.addCategory(
                requireContext(),
                etName.text.toString(),
                spinnerParentCategory.selectedItemPosition.takeIf { it != 0 }
                    ?.let { categories[spinnerParentCategory.selectedItemPosition].id },
                checkboxHidden.isChecked
            )
                .doOnSubscribe {
                    requireActivity().runOnUiThread {
                        addButton.isEnabled = false
                    }
                }
                .doOnTerminate {
                    requireActivity().runOnUiThread {
                        addButton.isEnabled = true
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (result is Result.Success) {
                        doOnSuccess.invoke(result.value)
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