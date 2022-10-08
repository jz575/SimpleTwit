package com.codepath.apps.restclienttemplate

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codepath.apps.restclienttemplate.models.SavedTweet
import com.codepath.apps.restclienttemplate.models.SavedTweetDao

@Database(entities = [SavedTweet::class], version = 2)
abstract class MyDatabase : RoomDatabase() {
    abstract fun savedTweetDao(): SavedTweetDao?

    companion object {
        // Database name to be used
        const val NAME = "MyDatabase"
    }
}