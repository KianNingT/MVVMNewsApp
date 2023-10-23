package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androiddevs.mvvmnewsapp.repository.NewsRepository

class NewsViewModelFactory(
    val app: Application,
    val newsRepository: NewsRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return (NewsViewModel(newsRepository, app) as T)
    }
}


//TODO use this
//class NewsViewModelFactory(private val repository: NewsRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
//            return NewsViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}