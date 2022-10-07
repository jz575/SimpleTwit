package com.codepath.apps.restclienttemplate.models

import org.json.JSONObject

class User {
    var name: String = ""
    var screenName: String = ""
    var publicImageUrl: String = ""

    companion object {
        fun fromJson(jsonObject: JSONObject): User {
            val user = User()
            user.name = jsonObject.getString("name")
            user.screenName = jsonObject.getString("screen_name")
            user.publicImageUrl = jsonObject.getString("profile_image_url_https")
            return user
        }
        fun stringFromJson(jsonObject: JSONObject): ArrayList<String> {
            val strings = ArrayList<String>()
            val user = jsonObject.getJSONObject("user")
            strings.add(user.getString("name"))
            strings.add(user.getString("screen_name"))
            strings.add(user.getString("profile_image_url_https"))
            return strings
        }
    }
}