package ru.neofusion.undead.myexpenses.ui.categories

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.databinding.ListItemCategoryBinding
import ru.neofusion.undead.myexpenses.domain.Category

class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private var categories: List<Category>? = null

    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): CategoryViewHolder {
        val categoryListItemBinding: ListItemCategoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.list_item_category, viewGroup, false
        )
        return CategoryViewHolder(categoryListItemBinding)
    }

    override fun onBindViewHolder(@NonNull categoryViewHolder: CategoryViewHolder, i: Int) {
        val category = categories?.get(i)
        categoryViewHolder.categoryListItemBinding.category = category
    }

    override fun getItemCount(): Int =
        categories?.size ?: 0

    fun setCategories(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    class CategoryViewHolder(val categoryListItemBinding: ListItemCategoryBinding) :
        RecyclerView.ViewHolder(categoryListItemBinding.root)
}