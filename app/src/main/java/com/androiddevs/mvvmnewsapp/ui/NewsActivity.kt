package com.androiddevs.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.google.android.material.bottomnavigation.BottomNavigationView


class NewsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    lateinit var mViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val repository = NewsRepository(ArticleDatabase(this))

        //TODO use this
//        val viewModel: NewsViewModel by viewModels {
//            NewsViewModelFactory(repository)
//        }
        val viewModelProviderFactory = NewsViewModelFactory(application, repository)
        mViewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment

        bottomNavigationView.setupWithNavController(navHostFragment.navController)

    }
}
