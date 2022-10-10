package com.codepath.apps.restclienttemplate

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.SavedTweet
import com.codepath.apps.restclienttemplate.models.SavedTweetDao
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

private val TAG = "TimelineActivity"
val REQUEST_CODE= 10
class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    lateinit var swipeContainer: SwipeRefreshLayout

    var savedTweetDao: SavedTweetDao? = null

    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.mipmap.ic_launcher)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        client = TwitterApplication.getRestClient(this)
        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(this, tweets)
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter

        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing Timeline")
            populateHomeTimeline()
        }
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        populateHomeTimeline()
        if(!isNetworkAvailable()) Toast.makeText(this,"No Internet",Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.compose) {
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            val tweet = data?.getParcelableExtra("tweet") as Tweet
            tweets.add(0, tweet)
            adapter.notifyDataSetChanged()
            rvTweets.smoothScrollToPosition(0)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun populateHomeTimeline() {
        client.getHomeTimeline(object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG,"on Success API call successful")

                val jsonArray = json.jsonArray

                try {
                    adapter.clear()
                    val newTweets = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(newTweets)
                    adapter.notifyDataSetChanged()
                    swipeContainer.isRefreshing = false
                    //add to database
                    val models = ArrayList<SavedTweet>()
                    var savedTweet: SavedTweet
                    savedTweetDao = (applicationContext as TwitterApplication).myDatabase?.savedTweetDao()
                    for(i in 0 until tweets.size){
                        savedTweet = SavedTweet()
                        savedTweet.id = i.toLong()
                        savedTweet.body = tweets[i].body
                        savedTweet.createdAt = tweets[i].createdAt
                        savedTweet.name = tweets[i].user[0]
                        savedTweet.screenName = tweets[i].user[1]
                        savedTweet.publicImageUrl = tweets[i].user[2]
                        models.add(savedTweet)
                    }
                    for(i in 0 until tweets.size){
                        AsyncTask.execute { savedTweetDao?.insertModel(models[i]) }
                    }
                } catch (e:JSONException){
                    Log.e(TAG,"JSON Exception $e")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG,"API call failed $statusCode")
                try {
                    savedTweetDao = (applicationContext as TwitterApplication).myDatabase?.savedTweetDao()
                    val savedTweets = savedTweetDao?.recentItems()
                    val newTweets = Tweet.fromSavedTweets(savedTweets)
                    tweets.addAll(newTweets)
                    swipeContainer.isRefreshing = false
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Log.e(TAG, "Could not update adapter")
                }
            }
        })
    }
    fun isNetworkAvailable(): Boolean {

        val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}