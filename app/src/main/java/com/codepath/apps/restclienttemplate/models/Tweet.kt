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
        fun fromSampleModel(sampleModel: SampleModel?) : Tweet {
            val body = sampleModel?.body ?: ""
            val createdAt = sampleModel?.createdAt ?: ""
            val user = ArrayList<String>()
            val name = sampleModel?.name ?: ""
            val screenName = sampleModel?.screenName ?: ""
            val publicImgUrl = sampleModel?.publicImageUrl ?: ""
            user.add(name)
            user.add(screenName)
            user.add(publicImgUrl)
            val tweet = Tweet(body, createdAt, user)
            return tweet
        }
        fun fromSampleModels(sampleModels: List<SampleModel?>?) : List<Tweet> {
            val tweets = ArrayList<Tweet>()
            val size = sampleModels?.size ?: 0
            for(i in 0 until size){
                tweets.add(fromSampleModel(sampleModels?.get(i)))
            }
            return tweets
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}