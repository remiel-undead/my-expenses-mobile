package ru.neofusion.undead.myexpenses.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import ru.neofusion.undead.myexpenses.domain.Result

abstract class BaseViewModelFragment<T : Any?> : Fragment() {
    @LayoutRes
    abstract fun getLayoutResource(): Int

    abstract fun getViewModel(): ResultViewModel<T>

    abstract fun doOnResult(result: Result<T>)

    private lateinit var viewModel: ResultViewModel<T>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(getLayoutResource(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel()
        viewModel.result.observe(this, Observer { result ->
            doOnResult(result)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.subscribe(requireContext())
    }
}