package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import com.codepath.apps.restclienttemplate.models.SavedTweet
import com.codepath.apps.restclienttemplate.models.SavedTweetDao
import com.codepath.oauth.OAuthLoginActionBarActivity

private val TAG = "LoginActivity"
class LoginActivity : OAuthLoginActionBarActivity<TwitterClient>() {

    var savedTweetDao: SavedTweetDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.mipmap.ic_launcher)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        val savedTweet = SavedTweet()
        savedTweet.name = "CodePath"
        savedTweetDao = (applicationContext as TwitterApplication).myDatabase?.savedTweetDao()
        //AsyncTask.execute { sampleModelDao?.insertModel(sampleModel) }
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    override fun onLoginSuccess() {
        // val i = Intent(this, PhotosActivity::class.java)
        // startActivity(i)
        Log.i(TAG,"Logged in Successfully")
        val i = Intent(this, TimelineActivity::class.java)
        startActivity(i)
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    override fun onLoginFailure(e: Exception) {
        Log.e(TAG,"Login Failed")
        e.printStackTrace()
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    fun loginToRest(view: View?) {
        client.connect()
    }
}