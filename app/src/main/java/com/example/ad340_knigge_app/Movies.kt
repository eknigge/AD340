package com.example.ad340_knigge_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class Movies : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        // Set action bar title
        supportActionBar?.title = "Movies"

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get data from DataSource
        val movieData = DataSource().getMoviesList()

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = MovieAdapter(movieData)

    }
}