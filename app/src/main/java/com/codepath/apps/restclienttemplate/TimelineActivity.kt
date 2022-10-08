package com.codepath.apps.restclienttemplate

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.SampleModel
import com.codepath.apps.restclienttemplate.models.SampleModelDao
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

private val TAG = "TimelineActivity"
class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    lateinit var swipeContainer: SwipeRefreshLayout

    var sampleModelDao: SampleModelDao? = null

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
                    val models = ArrayList<SampleModel>()
                    var sampleModel: SampleModel
                    sampleModelDao = (applicationContext as TwitterApplication).myDatabase?.sampleModelDao()
                    for(i in 0 until tweets.size){
                        sampleModel = SampleModel()
                        sampleModel.id = i.toLong()
                        sampleModel.body = tweets[i].body
                        sampleModel.createdAt = tweets[i].createdAt
                        sampleModel.name = tweets[i].user[0]
                        sampleModel.screenName = tweets[i].user[1]
                        sampleModel.publicImageUrl = tweets[i].user[2]
                        models.add(sampleModel)
                    }
                    for(i in 0 until tweets.size){
                        AsyncTask.execute { sampleModelDao?.insertModel(models[i]) }
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
                    sampleModelDao = (applicationContext as TwitterApplication).myDatabase?.sampleModelDao()
                    val sampleModels = sampleModelDao?.recentItems()
                    val newTweets = Tweet.fromSampleModels(sampleModels)
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