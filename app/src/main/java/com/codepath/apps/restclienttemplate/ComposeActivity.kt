package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private val TAG = "ComposeActivity"
class ComposeActivity : AppCompatActivity() {
    lateinit var client: TwitterClient
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweet)
        btnTweet = findViewById(R.id.btnTweet)
        client = TwitterApplication.getRestClient(this)

        btnTweet.setOnClickListener() {
            //grab text from
            val etContent = (etCompose.text).toString()
            //data validation
            //1. not empty
            if(etContent.isEmpty()) Toast.makeText(this, "Tweet is empty", Toast.LENGTH_SHORT).show()
            //2. not too long
            else if(etContent.length > 280) Toast.makeText(this, "Tweet must be less than 280 characters", Toast.LENGTH_SHORT).show()
            else {
                //make api call to publish tweet

                client.publishTweet(etContent, object: JsonHttpResponseHandler(){

                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        Log.i(TAG, "Successfully published tweet")
                        //send tweet back to TimelineActivity
                        val tweet = Tweet.fromJson(json.jsonObject)
                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }
                })
            }
        }
    }
}
