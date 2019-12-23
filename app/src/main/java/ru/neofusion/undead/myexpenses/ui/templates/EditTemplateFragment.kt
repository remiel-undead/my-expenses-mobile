package ru.neofusion.undead.myexpenses.ui.templates

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
import kotlinx.android.synthetic.main.fragment_edit_template.*
import kotlinx.android.synthetic.main.layout_edit_template_controls.*
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.TemplateActivity
import ru.neofusion.undead.myexpenses.domain.Category
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.domain.Template
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.ui.RoublesTextWatcher
import ru.neofusion.undead.myexpenses.ui.UiHelper

class EditTemplateFragment(
    private val templateId: Int
) : Fragment() {

    companion object {
        fun newInstance(
            templateId: Int
        ): EditTemplateFragment =
            EditTemplateFragment(templateId)
    }

    private lateinit var categories: List<Category>

    private val compositeDisposable = CompositeDisposable()

    private lateinit var viewModel: EditTemplateViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_template, container, false)
        retainInstance = true
        viewModel = ViewModelProviders.of(this).get(EditTemplateViewModel::class.java)
        viewModel.resultCategories.observe(this, androidx.lifecycle.Observer {
            doOnCategoriesResult(it)
        })
        viewModel.resultTemplate.observe(this, androidx.lifecycle.Observer {
            doOnTemplateResult(it)
        })
        return view
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }

    private fun doOnTemplateResult(result: Result<Template>) {
        if (result is Result.Success) {
            val template = result.value
            initAdapter(template.category)

            initControls(template)
        } else {
            UiHelper.snack(requireActivity(), (result as Result.Error).message)
        }
    }

    private fun doOnCategoriesResult(result: Result<List<Category>>) {
        if (result is Result.Success) {
            categories = result.value.filterNot { it.hidden }
        } else {
            UiHelper.snack(requireActivity(), (result as Result.Error).message)
        }
    }

    private fun initAdapter(category: Category) {
        if (categories.firstOrNull { it.id == category.id } == null) {
            categories = categories.plus(category)
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            categories.map { it.name }
        )
        spinnerCategory.adapter = adapter
        if (categories.isNotEmpty()) {
            spinnerCategory.setSelection(0)
        } else {
            UiHelper.snack(requireActivity(), getString(R.string.error_no_categories))
            requireActivity().finish()
        }
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton.setOnClickListener {
            saveTemplate { templateId ->
                finishWithSuccess(templateId)
            }
        }

        etCost.addTextChangedListener(RoublesTextWatcher(etCost))

        viewModel.subscribe(requireContext(), templateId)
    }

    private fun initControls(template: Template) {
        spinnerCategory.adapter.count.takeIf { it > 0 }.let {
            val index = categories.indexOfFirst { it.id == template.category.id }
            spinnerCategory.setSelection(if (index != -1) index else 0)
        }
        etDescription.setText(template.description.orEmpty())
        etSeller.setText(template.seller.orEmpty())
        etCost.setText(template.cost)
    }

    private fun saveTemplate(doOnSuccess: (Int) -> Unit) {
        if (!areFieldsValid()) {
            return
        }

        compositeDisposable.add(
            MyExpenses.TemplateApi.editTemplate(
                requireContext(),
                templateId,
                categories[spinnerCategory.selectedItemPosition].id,
                etDescription.text.toString(),
                etSeller.text.toString(),
                etCost.text.toString()
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
                        doOnSuccess.invoke(templateId)
                    } else {
                        UiHelper.snack(requireActivity(), (result as Result.Error).message)
                    }
                }, {
                    UiHelper.snack(requireActivity(), it.message ?: "Ой-ой-ой")
                })
        )
    }

    private fun finishWithSuccess(templateId: Int) {
        requireActivity().setResult(
            RESULT_OK,
            Intent().apply {
                TemplateActivity.putTemplateId(this, templateId)
            })
        requireActivity().finish()
    }

    private fun areFieldsValid(): Boolean {
        var result = true
        etCost.text.takeIf { it.isNullOrEmpty() }?.let {
            etCost.error = getString(R.string.error_empty)
            result = false
        }
        return result
    }
}