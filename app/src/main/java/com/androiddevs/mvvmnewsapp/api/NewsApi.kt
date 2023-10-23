package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "us", // default value
        @Query("page")
        pageNumber: Int = 1, // to get only 20 articles at once in a page
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String, // no default value
        @Query("page")
        pageNumber: Int = 1, // to get only 20 articles at once in a page
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}