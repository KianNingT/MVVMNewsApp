package com.androiddevs.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.models.Article

@Database(
    //how many tables in your database. So far only 1 (Article)
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase: RoomDatabase() {

    //create a function that returns ArticleDao
    //implementation will happen behind the scenes. Room will do that
    abstract fun getArticleDao(): ArticleDao

    companion object {
        //means other threads can immediately see when a thread changes this instance
        @Volatile
        private var instance: ArticleDatabase? = null

        //to make sure only single instance is available
        private val LOCK = Any()

        //create database
        //context is the application context
        //invoke is a function that can be called without a name
        //if instance is null, create database
        //if instance is not null, return instance
        //synchronized means only one thread can access this block of code at a time
        //if instance is null, create database. If instance is not null, return instance
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            //if instance is null, create database
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            //build database
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}