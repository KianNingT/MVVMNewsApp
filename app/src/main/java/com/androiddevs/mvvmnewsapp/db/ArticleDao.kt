package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.mvvmnewsapp.models.Article
import retrofit2.http.DELETE

//Dao is like the Api interface in Retrofit (NewsApi). ArticleDao is the same except it is for local room database.
@Dao
interface ArticleDao {

    //if article we want to insert already exists in the database, we replace it with the new one.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //upsert meaning update or insert
    suspend fun upsert(article: Article): Long //returns the article id that was inserted

    //get all articles from the database
    //we don't use suspend function for getAllArticles because we want to observe the changes in the database and display it in live data
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    //delete article from the local room database
    @Delete
    suspend fun deleteArticle(article: Article)
}