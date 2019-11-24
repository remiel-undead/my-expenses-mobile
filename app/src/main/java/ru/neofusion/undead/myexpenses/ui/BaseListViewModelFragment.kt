package ru.neofusion.undead.myexpenses.ui

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.neofusion.undead.myexpenses.R

abstract class BaseListViewModelFragment<T : Any?> : BaseViewModelFragment<List<T>>() {
    override fun getLayoutResource(): Int = R.layout.fragment_base_list

    override fun loadViewData() {
        viewModel.subscribe(
            requireContext(),
            { swipeRefreshLayout.isRefreshing = true },
            { swipeRefreshLayout.isRefreshing = false })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener { loadViewData() }
    }
}