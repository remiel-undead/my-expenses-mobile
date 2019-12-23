package ru.neofusion.undead.myexpenses

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.neofusion.undead.myexpenses.ui.templates.AddTemplateFragment
import ru.neofusion.undead.myexpenses.ui.templates.EditTemplateFragment

class TemplateActivity : AppCompatActivity() {
    companion object {

        private const val KEY_TEMPLATE_ID = "templateId"
        private const val KEY_CATEGORY_ID = "categoryId"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_SELLER = "seller"
        private const val KEY_COST_STRING = "costString"

        fun getTemplateId(bundle: Bundle?) = bundle?.getInt(KEY_TEMPLATE_ID, -1) ?: -1

        fun getTemplateCategoryId(bundle: Bundle?) = bundle?.getInt(KEY_CATEGORY_ID, -1) ?: -1

        fun getDescription(bundle: Bundle?) = bundle?.getString(KEY_DESCRIPTION)

        fun getSeller(bundle: Bundle?) = bundle?.getString(KEY_SELLER)

        fun getCostString(bundle: Bundle?) = bundle?.getString(KEY_COST_STRING)

        fun putTemplateId(intent: Intent, templateId: Int) {
            intent.putExtra(KEY_TEMPLATE_ID, templateId)
        }

        fun putCategoryId(intent: Intent, categoryId: Int?) {
            categoryId?.let { intent.putExtra(KEY_CATEGORY_ID, it) }
        }

        fun putDescription(intent: Intent, description: String?) {
            intent.putExtra(KEY_DESCRIPTION, description)
        }

        fun putSeller(intent: Intent, seller: String?) {
            intent.putExtra(KEY_SELLER, seller)
        }

        fun putCostString(intent: Intent, costString: String?) {
            intent.putExtra(KEY_COST_STRING, costString)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template)
        val bundle = intent.extras
        val templateId = getTemplateId(bundle)
        supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.templateFragment,
                if (templateId != -1)
                    EditTemplateFragment.newInstance(templateId)
                else
                    AddTemplateFragment.newInstance(
                        getTemplateCategoryId(bundle).takeIf { it != -1 },
                        getDescription(bundle),
                        getSeller(bundle),
                        getCostString(bundle)
                    )
            )
            commit()
        }
    }
}