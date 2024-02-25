package com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newzbreak.common.NewsResource
import com.example.newzbreak.mvvm.repository.NewsRepository
import com.example.newzbreak.ui_layers.NewsApplication
import com.example.newzbreak.ui_layers.model.Article
import com.example.newzbreak.ui_layers.model.NewsResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException


class NewsActivityViewModel(app: Application, var repository: NewsRepository) : AndroidViewModel(app) {

    var breakingNews: MutableLiveData<NewsResource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsPageResponse: NewsResponse? = null

    var searchNews: MutableLiveData<NewsResource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsPageResponse: NewsResponse? = null

    init {
        getBreakingNews("in")
    }

    // breaking news
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeSearchBreakingNews(countryCode)
    }

    private suspend fun safeSearchBreakingNews(countryCode: String) {
        breakingNews.postValue(NewsResource.Loading())
        if (isDeviceConnectedToInternet()) {
            try {
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> {
                        breakingNews.postValue(NewsResource.Error("Network Error"))
                    }

                    else -> {
                        breakingNews.postValue(NewsResource.Error("${t.message}"))
                    }
                }
            }
        } else {
            breakingNews.postValue(NewsResource.Error("No Internet connection"))
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): NewsResource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                breakingNewsPage++
                if (breakingNewsPageResponse == null) {     // first page
                    breakingNewsPageResponse = newsResponse
                } else {                                    // pagination
                    val oldArticles = breakingNewsPageResponse?.articles
                    val newArticles = newsResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return NewsResource.Success(breakingNewsPageResponse ?: newsResponse)
            }
        }
        return NewsResource.Error(response.message())
    }

    // search news
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeCallSearchNews(searchQuery)
    }

    private suspend fun safeCallSearchNews(searchQuery: String) {
        searchNews.postValue(NewsResource.Loading())
        if (isDeviceConnectedToInternet()) {
            try {
                val response = repository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> {
                        searchNews.postValue(NewsResource.Error("Network error"))
                    }

                    else -> {
                        searchNews.postValue(NewsResource.Error("${t.message}"))
                    }
                }
            }
        } else {
            searchNews.postValue(NewsResource.Error("Internet not connected"))
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): NewsResource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                searchNewsPage++
                if (searchNewsPageResponse == null) {
                    searchNewsPageResponse = newsResponse
                } else {
                    val oldArticle = searchNewsPageResponse?.articles
                    val newArticle = newsResponse.articles
                    oldArticle?.addAll(newArticle)
                }
                return NewsResource.Success(searchNewsPageResponse ?: newsResponse)
            }
        }
        return NewsResource.Error(response.message())

    }

    // check internet connection
    private fun isDeviceConnectedToInternet(): Boolean {
        val connectivityManager =
            getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                else -> false
            }

        } else {

            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_ETHERNET -> true
                    TYPE_MOBILE -> true
                    else -> false
                }
            }

        }

        return false
    }

    // room db

    fun upsert(article: Article) = viewModelScope.launch {
        repository.upsert(article)
    }

    fun delete(article:Article) = viewModelScope.launch {
        repository.delete(article)
    }

    fun getSavedArticles() : LiveData<List<Article>> = repository.getSavedArticles()

}