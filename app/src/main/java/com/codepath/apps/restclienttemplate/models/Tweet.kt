package com.codepath.apps.restclienttemplate.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
data class Tweet (
    var body: String,
    var createdAt: String,
    var user: ArrayList<String>
) : Parcelable {
    companion object {
        fun fromJson(jsonObject: JSONObject): Tweet {
            val tweet = Tweet(
                jsonObject.getString("text"),
                jsonObject.getString("created_at"),
                User.stringFromJson(jsonObject)
            )
            return tweet
        }
        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}