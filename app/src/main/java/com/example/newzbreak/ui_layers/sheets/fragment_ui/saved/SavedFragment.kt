package com.example.newzbreak.ui_layers.sheets.fragment_ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.newzbreak.R
import com.example.newzbreak.databinding.FragmentSavedBinding
import com.example.newzbreak.ui_layers.adapter.NewsAdapter
import com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity.NewsActivity
import com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity.NewsActivityViewModel
import com.google.android.material.snackbar.Snackbar

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsActivityViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()

        newsAdapter.onItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_savedFragment_to_articlesFragment, bundle)
        }

        viewModel.getSavedArticles().observe(viewLifecycleOwner) {
            it?.let { savedArticles ->
                newsAdapter.differ.submitList(savedArticles)
            }
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT ->  deleteArticle(view, viewHolder.adapterPosition)
                    ItemTouchHelper.RIGHT -> deleteArticle(view, viewHolder.adapterPosition)
                }
            }

        }).attachToRecyclerView(binding.savedArticleRv)
    }

    private fun setupRV() {
        newsAdapter = NewsAdapter()
        binding.savedArticleRv.apply {
            adapter = newsAdapter
        }
    }

    private fun deleteArticle(view: View, articlePosition:Int){
        val article = newsAdapter.differ.currentList[articlePosition]
        article?.let {
            viewModel.delete(it)
        }

        Snackbar.make(view, "Article deleted", Snackbar.LENGTH_LONG).apply {
            setAction("UNDO"){
                article?.let {
                    viewModel.upsert(it)
                }
            }
        }.show()

    }

}