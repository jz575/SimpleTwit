package com.codepath.apps.restclienttemplate

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.apps.restclienttemplate.models.User
import java.text.ParseException
import java.util.*

private const val TAG = "DetailActivity"
private val SECOND_MILLIS = 1000;
private val MINUTE_MILLIS = 60 * SECOND_MILLIS
private val HOUR_MILLIS = 60 * MINUTE_MILLIS
private val DAY_MILLIS = 24 * HOUR_MILLIS
class DetailActivity : AppCompatActivity() {

    private lateinit var ivProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvScreenName: TextView
    private lateinit var tvTimestamp: TextView
    private lateinit var tvTweet: TextView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        ivProfile = findViewById(R.id.ivProfile)
        tvUsername = findViewById(R.id.tvUsername)
        tvScreenName = findViewById(R.id.tvScreenName)
        tvTimestamp = findViewById(R.id.tvTimestamp)
        tvTweet = findViewById(R.id.tvTweet)

        val tweet = intent.getParcelableExtra<Tweet>(TWEET_EXTRA) as Tweet
        val user = tweetToUser(tweet)
        Glide
            .with(this)
            .load(user.publicImageUrl)
            .into(ivProfile)
        tvUsername.text = user?.name
        tvScreenName.text = user?.screenName
        tvTimestamp.text = getRelativeTimeAgo(tweet.createdAt)
        tvTweet.text = tweet.body
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun getRelativeTimeAgo(rawJsonDate: String): String{
        val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
        val sf = SimpleDateFormat(twitterFormat, Locale.ENGLISH)

        try {
            val time = sf.parse(rawJsonDate).time
            val now = System.currentTimeMillis()

            val diff = now - time
            if(diff < MINUTE_MILLIS) return "just now"
            else if(diff < 2 * MINUTE_MILLIS) return "a minute ago"
            else if(diff < 60 * MINUTE_MILLIS){
                val mins = diff / MINUTE_MILLIS
                return "$mins m"
            } else if(diff < 120 * MINUTE_MILLIS) return "an hour ago"
            else if(diff < 24 * HOUR_MILLIS){
                val hours = diff / HOUR_MILLIS
                return "$hours h"
            } else if(diff < 48 * HOUR_MILLIS) return "yesterday"
            else {
                val days = diff / DAY_MILLIS
                return "$days d"
            }
        } catch (e: ParseException) {
            Log.e(TAG, "Relative time Error $e")
        }
        return ""
    }
    fun tweetToUser(tweet: Tweet) : User {
        val user = User()
        user?.name = tweet.user[0]
        user?.screenName = tweet.user[1]
        user?.publicImageUrl = tweet.user[2]
        return user
    }
}