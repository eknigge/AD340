package com.example.ad340_knigge_app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MovieDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        // Set action bar title
        supportActionBar?.title = "Movie Details"

        val movieData = intent.extras?.getStringArray("movieDetails")
        findViewById<TextView>(R.id.movie_detail_title).text = movieData?.get(0)
        findViewById<TextView>(R.id.movie_detail_year).text = movieData?.get(1)
        findViewById<TextView>(R.id.movie_detail_summary).text = movieData?.get(4)

    }
}