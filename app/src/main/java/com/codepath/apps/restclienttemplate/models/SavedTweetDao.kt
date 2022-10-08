package com.codepath.apps.restclienttemplate.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedTweetDao {

    // @Query annotation requires knowing SQL syntax
    // See http://www.sqltutorial.org/
    @Query("SELECT * FROM SavedTweet WHERE id = :id")
    fun byId(id: Long): SavedTweet?

    @Query("SELECT * FROM SavedTweet ORDER BY ID ASC LIMIT 15")
    fun recentItems(): List<SavedTweet?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertModel(vararg savedTweets: SavedTweet?)
}