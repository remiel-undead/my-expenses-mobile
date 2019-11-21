package ru.neofusion.undead.myexpenses.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_categories.*
import ru.neofusion.undead.myexpenses.R
import ru.neofusion.undead.myexpenses.domain.Result

class CategoriesFragment : Fragment() {

    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_categories, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriesViewModel =
            ViewModelProviders.of(this).get(CategoriesViewModel::class.java)
        categoriesAdapter = CategoriesAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = categoriesAdapter
        categoriesViewModel.subscribe(requireContext())
        categoriesViewModel.result.observe(this, Observer { result ->
            if (result is Result.Success) {
                categoriesAdapter.setCategories(result.value)
                emptyListTextView.visibility = if (result.value.isEmpty()) View.VISIBLE else View.GONE
            }
        })
    }
}