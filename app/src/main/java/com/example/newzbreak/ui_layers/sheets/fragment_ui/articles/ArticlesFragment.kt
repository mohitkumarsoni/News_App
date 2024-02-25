package com.example.newzbreak.ui_layers.sheets.fragment_ui.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newzbreak.databinding.FragmentArticlesBinding
import com.example.newzbreak.ui_layers.model.Article
import com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity.NewsActivity
import com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity.NewsActivityViewModel
import com.google.android.material.snackbar.Snackbar

class ArticlesFragment : Fragment() {

    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsActivityViewModel
    private lateinit var article: Article
    val args: ArticlesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        article = args.article

        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        binding.saveFab.setOnClickListener {
            viewModel.upsert(article)
            Snackbar.make(view, "Article Saved", Snackbar.LENGTH_SHORT).show()
        }

    }

}