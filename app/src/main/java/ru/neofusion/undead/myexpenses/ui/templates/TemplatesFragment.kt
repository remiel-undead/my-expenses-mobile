package ru.neofusion.undead.myexpenses.ui.templates

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.TemplateActivity
import ru.neofusion.undead.myexpenses.domain.Template
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.repository.network.MyExpenses
import ru.neofusion.undead.myexpenses.ui.BaseListViewModelFragment
import ru.neofusion.undead.myexpenses.ui.ResultViewModel
import ru.neofusion.undead.myexpenses.ui.UiHelper

class TemplatesFragment : BaseListViewModelFragment<Template>() {
    companion object {
        private const val REQUEST_CODE_ADD_TEMPLATE = 1000
        private const val REQUEST_CODE_EDIT_TEMPLATE = 1001
    }

    interface TemplateLongClickListener {
        fun onTemplateLongClick(template: Template)
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var templatesAdapter: TemplatesAdapter
    private lateinit var longClickOptions: Array<String>

    private val templateLongClickListener = object : TemplateLongClickListener {
        override fun onTemplateLongClick(template: Template) {
            val dialog = AlertDialog.Builder(requireContext())
                .setItems(longClickOptions) { _, which ->
                    when (which) {
                        0 -> { // edit
                            val intent = Intent(activity, TemplateActivity::class.java)
                            TemplateActivity.putTemplateId(intent, template.id)
                            startActivityForResult(intent, REQUEST_CODE_EDIT_TEMPLATE)

                        }
                        1 -> { // delete
                            showDeleteTemplateDialog(template.id)
                        }
                    }
                }.create()
            dialog.show()
        }
    }

    private fun showDeleteTemplateDialog(templateId: Int) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_template_dialog_message)
            .setPositiveButton(R.string.button_text_delete) { dialog, _ ->
                compositeDisposable.add(
                    MyExpenses.TemplateApi.deleteTemplate(requireContext(), templateId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            if (result is Result.Success) {
                                UiHelper.snack(requireActivity(), "Шаблон $templateId удален")
                            } else {
                                UiHelper.snack(requireActivity(), (result as Result.Error).message)
                            }
                        }, {
                            UiHelper.snack(requireActivity(), it.message ?: "Ой-ой-ой")
                        })
                )
                dialog.dismiss()
            }
            .setNegativeButton(R.string.button_text_cancel) { dialog, _ ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    override val viewModel: ResultViewModel<List<Template>>
        get() = ViewModelProviders.of(this).get(TemplatesViewModel::class.java)

    override fun doOnResult(result: Result<List<Template>>) {
        if (result is Result.Success) {
            templatesAdapter.setTemplates(result.value)
            emptyListTextView.visibility = if (result.value.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        templatesAdapter = TemplatesAdapter(templateLongClickListener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = templatesAdapter

        addButton.setOnClickListener {
            startActivityForResult(
                Intent(activity, TemplateActivity::class.java), REQUEST_CODE_ADD_TEMPLATE
            )
        }
        longClickOptions = arrayOf(
            getString(R.string.long_tap_option_edit),
            getString(R.string.long_tap_option_delete)
        )
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }
}