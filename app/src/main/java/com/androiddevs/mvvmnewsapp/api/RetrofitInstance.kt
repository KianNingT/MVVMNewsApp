package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    //lazy means we will only initialize here once
    private val retrofit by lazy {

        /*attach loggin interceptor to retrofit object to see what request we are making and
         also to get the responses in log form*/
        val logging = HttpLoggingInterceptor()
        //see the body of our response
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        Retrofit.Builder()
            //base url
            .baseUrl(BASE_URL)
            //google implementation of json converting
            .addConverterFactory(
                GsonConverterFactory.create())
            //add client
            .client(client)
            //build retrofit
            .build()
    }

    val api by lazy {
        retrofit.create(NewsApi::class.java)
    }

}