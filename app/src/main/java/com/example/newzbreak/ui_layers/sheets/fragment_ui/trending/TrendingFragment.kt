package com.example.newzbreak.ui_layers.sheets.fragment_ui.trending

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newzbreak.R
import com.example.newzbreak.common.NewsResource
import com.example.newzbreak.common.QUERY_PAGE_SIZE
import com.example.newzbreak.common.hideProgressBar
import com.example.newzbreak.common.showProgressBar
import com.example.newzbreak.databinding.FragmentTrendingBinding
import com.example.newzbreak.ui_layers.adapter.NewsAdapter
import com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity.NewsActivity
import com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity.NewsActivityViewModel

class TrendingFragment : Fragment() {

    private var _binding : FragmentTrendingBinding ?= null
    private val binding get() = _binding!!

    private lateinit var viewModel : NewsActivityViewModel
    private lateinit var newsAdapter: NewsAdapter
    private val TAG = "TrendingFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentTrendingBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()

        // observing data
        viewModel.breakingNews.observe(viewLifecycleOwner){
            when(it){
                is NewsResource.Success -> {
                    hideProgressBar(binding.paginationPb)
                    isLoading = false
                    it.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())

                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE +2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                    }
                }
                is NewsResource.Error -> {
                    isLoading = false
                    hideProgressBar(binding.paginationPb)
                    it.message?.let {errorMessage ->
                        Log.d(TAG, "onViewCreated: $errorMessage")
                        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                is NewsResource.Loading -> {
                    showProgressBar(binding.paginationPb)
                    isLoading = true
                }
                else -> {}
            }
        }

        // on click nav to article
        newsAdapter.onItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_trendingFragment_to_articlesFragment, bundle)
        }

    }

    private fun setupRV(){
        newsAdapter = NewsAdapter()
        binding.trendingRv.apply {
            adapter = newsAdapter
            addOnScrollListener(this@TrendingFragment.scrollListener)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItmCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingNotAtLastPage = isLoading.not() && isLastPage.not()
            val isAtLastItem = firstVisibleItemPosition + visibleItmCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingNotAtLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
            if (shouldPaginate){
                viewModel.getBreakingNews("in")
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

    }

}