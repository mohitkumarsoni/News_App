package com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newzbreak.R
import com.example.newzbreak.databinding.ActivityNewsBinding
import com.example.newzbreak.mvvm.repository.NewsRepository
import com.example.newzbreak.mvvm.room_db.NewsRoomDb

class NewsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityNewsBinding.inflate(layoutInflater)
    }

    lateinit var viewModel:NewsActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.navView.setupWithNavController(supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)!!.findNavController())

        val repository = NewsRepository(NewsRoomDb.invoke(this))
        val factory = NewsActivityViewModelProvider(application,repository)

        viewModel = ViewModelProvider(this, factory)[NewsActivityViewModel::class.java]

    }
}