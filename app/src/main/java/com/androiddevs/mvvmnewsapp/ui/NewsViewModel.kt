package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager

import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    val repository: NewsRepository,
    val app: Application
): AndroidViewModel(app) {

    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()


    //put breakingNewsPage in viewModel because it will not reset the Int value when we rotate the screen
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.upsert(article)
    }

    //not a suspend. no need to call coroutine. can just observe the changes in the function
    fun getSavedNews() = repository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        //we will now decide if we will emit success state or error state of live data
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                //pagination logic below
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        //we will now decide if we will emit success state or error state of live data
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                //pagination logic below
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        //we want to emit the loading state
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                //we want to get the response from the repository
                val response = repository.searchNews(searchQuery, searchNewsPage)
                //we want to emit the response from the repository
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            //have wifi but no internet
            //OR have internet but somehow still fail
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                //conversion error. Conversion from Json to Kotlin object failed somehiow
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        //we want to emit the loading state
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                //we want to get the response from the repository
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
                //we want to emit the response from the repository
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            //have wifi but no internet
            //OR have internet but somehow still fail
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                //conversion error. Conversion from Json to Kotlin object failed somehiow
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
             connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}