package com.example.newzbreak.ui_layers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.newzbreak.databinding.EachArticlePreviewBinding
import com.example.newzbreak.ui_layers.model.Article

class NewsAdapter : Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(var binding: EachArticlePreviewBinding) : ViewHolder(binding.root)

    private var diffCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    var differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            EachArticlePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.binding.apply {
            dateAndTimeTv.text = article.publishedAt
            titleTv.text = article.title
            descriptionTv.text = article.description
            newsImageIv.load(article.urlToImage)
            sourceTv.text = article.source?.name
        }

        holder.itemView.setOnClickListener {
            itemClickListener?.let {
                it(article)
            }
        }

    }

    private var itemClickListener: ((Article) -> Unit?)? = null

    fun onItemClickListener(listener: (Article) -> Unit) {
        itemClickListener = listener
    }

}