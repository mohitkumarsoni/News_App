package com.example.newzbreak.ui_layers.sheets.fragment_ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newzbreak.R
import com.example.newzbreak.common.NewsResource
import com.example.newzbreak.common.QUERY_PAGE_SIZE
import com.example.newzbreak.common.SEARCH_DELAY_500
import com.example.newzbreak.common.hideProgressBar
import com.example.newzbreak.common.showProgressBar
import com.example.newzbreak.databinding.FragmentSearchBinding
import com.example.newzbreak.ui_layers.adapter.NewsAdapter
import com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity.NewsActivity
import com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity.NewsActivityViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsActivityViewModel
    private lateinit var newsAdapter: NewsAdapter
    val TAG = "SearchFragment"

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()

        var job: Job? = null
        binding.searchNewsEt.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_DELAY_500)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(binding.searchNewsEt.text.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner){
            when(it){
                is NewsResource.Success -> {
                    hideProgressBar(binding.searchNewsPb)
                    isLoading = false

                    it.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                    }
                }
                is NewsResource.Error -> {
                    hideProgressBar(binding.searchNewsPb)
                    isLoading = false

                    it.message?.let {errorMessage ->
                        Log.d(TAG, "onViewCreated: $errorMessage")
                        Toast.makeText(activity, errorMessage , Toast.LENGTH_SHORT).show()
                    }
                }
                is NewsResource.Loading -> {
                    showProgressBar(binding.searchNewsPb)
                    isLoading = true
                }
                else -> {}
            }
        }

        newsAdapter.onItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_articlesFragment, bundle)
        }

    }

    private fun setupRV() {
        newsAdapter = NewsAdapter()
        binding.searchNewsRv.apply {
            adapter = newsAdapter
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private var scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingNotAtLastPage = isLoading.not() && isLastPage.not()
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition > 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = (isNotLoadingNotAtLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible)
            if (shouldPaginate){
                viewModel.searchNews(binding.searchNewsEt.text.toString())
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)  isScrolling = true
        }
    }

}