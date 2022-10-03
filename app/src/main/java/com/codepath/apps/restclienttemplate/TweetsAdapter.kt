package com.codepath.apps.restclienttemplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet

class TweetsAdapter(val tweets: List<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_tweet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tweet: Tweet = tweets.get(position)

        holder.tvUsername.text = tweet.user?.name
        holder.tvTweet.text = tweet.body
        Glide.with(holder.itemView)
            .load(tweet.user?.publicImageUrl)
            .into(holder.ivProfile)
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivProfile = itemView.findViewById<ImageView>(R.id.ivProfile)
        val tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvTweet = itemView.findViewById<TextView>(R.id.tvTweet)
    }
}