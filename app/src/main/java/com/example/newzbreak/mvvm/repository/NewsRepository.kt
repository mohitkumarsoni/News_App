package com.example.newzbreak.mvvm.repository

import androidx.lifecycle.LiveData
import com.example.newzbreak.mvvm.retrofit_api.RetrofitInstance
import com.example.newzbreak.mvvm.room_db.NewsRoomDb
import com.example.newzbreak.ui_layers.model.Article

class NewsRepository(var db: NewsRoomDb) {

    // api
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) = RetrofitInstance.api.searchNews(searchQuery, pageNumber)

    // room
    suspend fun upsert(article: Article) = db.newsDao().upsert(article)

    suspend fun delete(article: Article) = db.newsDao().delete(article)

    fun getSavedArticles(): LiveData<List<Article>> = db.newsDao().getSavedNews()

}