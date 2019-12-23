package ru.neofusion.undead.myexpenses.ui.templates

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.neofusion.undead.myexpenses.domain.Template
import ru.neofusion.undead.myexpenses.domain.Result
import ru.neofusion.undead.myexpenses.ui.BaseListViewModelFragment
import ru.neofusion.undead.myexpenses.ui.ResultViewModel

class TemplatesFragment : BaseListViewModelFragment<Template>() {
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
                        // TODO
                    }
                }.create()
            dialog.show()
        }
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
            // TODO
        }
        longClickOptions = arrayOf(
            // TODO
        )
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }
}