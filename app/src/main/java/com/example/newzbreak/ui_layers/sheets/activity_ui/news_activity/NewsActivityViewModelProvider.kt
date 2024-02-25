package com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newzbreak.mvvm.repository.NewsRepository

class NewsActivityViewModelProvider(var app: Application, var repository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsActivityViewModel(app, repository) as T
    }
}