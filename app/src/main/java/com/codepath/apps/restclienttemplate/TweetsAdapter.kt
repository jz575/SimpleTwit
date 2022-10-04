package com.codepath.apps.restclienttemplate

import android.icu.text.RelativeDateTimeFormatter
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet
import java.text.ParseException
import java.util.*
private val TAG = "TweetsAdapter"
private val SECOND_MILLIS = 1000;
private val MINUTE_MILLIS = 60 * SECOND_MILLIS;
private val HOUR_MILLIS = 60 * MINUTE_MILLIS;
private val DAY_MILLIS = 24 * HOUR_MILLIS;

class TweetsAdapter(val tweets: List<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_tweet, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tweet: Tweet = tweets.get(position)

        holder.tvUsername.text = tweet.user?.name
        holder.tvTweet.text = tweet.body
        Glide.with(holder.itemView)
            .load(tweet.user?.publicImageUrl)
            .into(holder.ivProfile)
        //convert to relative timestamp
        holder.tvTimestamp.text = getRelativeTimeAgo(tweet.createdAt)
    }

    override fun getItemCount(): Int {
        return tweets.size
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivProfile = itemView.findViewById<ImageView>(R.id.ivProfile)
        val tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvTweet = itemView.findViewById<TextView>(R.id.tvTweet)
        val tvTimestamp = itemView.findViewById<TextView>(R.id.tvTimestamp)
    }
}