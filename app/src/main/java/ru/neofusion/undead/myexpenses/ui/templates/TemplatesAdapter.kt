package ru.neofusion.undead.myexpenses.ui.templates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.databinding.ListItemTemplateBinding
import ru.neofusion.undead.myexpenses.domain.Template

class TemplatesAdapter(private val templateLongClickListener: TemplatesFragment.TemplateLongClickListener) :
    RecyclerView.Adapter<TemplatesAdapter.TemplateViewHolder>() {
    private var templates: List<Template>? = null

    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): TemplateViewHolder {
        val listItemTemplateBinding: ListItemTemplateBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.list_item_template, viewGroup, false
        )
        return TemplateViewHolder(listItemTemplateBinding)
    }

    override fun onBindViewHolder(@NonNull templateViewHolder: TemplateViewHolder, i: Int) {
        val template = templates?.get(i)
        templateViewHolder.listItemTemplateBinding.template = template
        template?.let {
            templateViewHolder.itemView.setOnLongClickListener {
                templateLongClickListener.onTemplateLongClick(template)
                true
            }
        }
    }

    override fun getItemCount(): Int =
        templates?.size ?: 0

    fun setTemplates(templates: List<Template>) {
        this.templates = templates
        notifyDataSetChanged()
    }

    class TemplateViewHolder(val listItemTemplateBinding: ListItemTemplateBinding) :
        RecyclerView.ViewHolder(listItemTemplateBinding.root)
}