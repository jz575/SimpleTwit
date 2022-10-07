package com.codepath.apps.restclienttemplate

import android.app.Activity
import android.content.Intent
import android.icu.text.RelativeDateTimeFormatter
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.apps.restclienttemplate.models.User
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList

private val TAG = "TweetsAdapter"
private val SECOND_MILLIS = 1000;
private val MINUTE_MILLIS = 60 * SECOND_MILLIS
private val HOUR_MILLIS = 60 * MINUTE_MILLIS
private val DAY_MILLIS = 24 * HOUR_MILLIS

const val TWEET_EXTRA = "TWEET_EXTRA"
class TweetsAdapter(private val context: Context, val tweets: ArrayList<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_tweet, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tweet = tweets[position]
        holder.bind(tweet)
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    fun addAll(tweetList: List<Tweet>) {
        tweets.addAll(tweetList)
        notifyDataSetChanged()
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val ivProfile = itemView.findViewById<ImageView>(R.id.ivProfile)
        val tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvTweet = itemView.findViewById<TextView>(R.id.tvTweet)
        val tvTimestamp = itemView.findViewById<TextView>(R.id.tvTimestamp)
        init {
            itemView.setOnClickListener(this)
        }
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(tweet: Tweet) {
            val user = tweetToUser(tweet)
            tvUsername.text = user.name
            tvTweet.text = tweet.body
            Glide.with(itemView)
                .load(user.publicImageUrl)
                .into(ivProfile)
            //convert to relative timestamp
            tvTimestamp.text = getRelativeTimeAgo(tweet.createdAt)
        }
        override fun onClick(v: View?) {
            val tweet = tweets[adapterPosition]
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(TWEET_EXTRA, tweet)
            val activity: Activity = context as Activity
            context.startActivity(intent)
        }
    }

}