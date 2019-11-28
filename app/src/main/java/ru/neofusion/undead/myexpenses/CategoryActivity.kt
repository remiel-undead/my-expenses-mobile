package ru.neofusion.undead.myexpenses

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.neofusion.undead.myexpenses.ui.categories.AddCategoryFragment
import ru.neofusion.undead.myexpenses.ui.categories.EditCategoryFragment

class CategoryActivity : AppCompatActivity() {
    companion object {

        private const val KEY_CATEGORY_ID = "categoryId"

        fun getCategoryId(bundle: Bundle?) = bundle?.getInt(KEY_CATEGORY_ID)

        fun putCategoryId(intent: Intent, categoryId: Int?) {
            categoryId?.let { intent.putExtra(KEY_CATEGORY_ID, it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        val bundle = intent.extras
        val categoryId = getCategoryId(bundle)
        supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.categoryFragment,
                if (categoryId != null)
                    EditCategoryFragment.newInstance(categoryId)
                else
                    AddCategoryFragment.newInstance()
            )
            commit()
        }
    }
}