package com.example.ad340_knigge_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView

class Movies : AppCompatActivity() {
    // Get data from DataSource
    val movieData = DataSource().getMoviesList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        // Set action bar title
        supportActionBar?.title = "Movies"

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = MovieAdapter(movieData) { position: Int -> onItemClick(position) }
    }

    fun onItemClick(position: Int){
        val selectedMovie = movieData[position]
        val intent = Intent(this, MovieDetail::class.java)
        intent.putExtra("movieDetails", selectedMovie)
        startActivity(intent)
        onPause()
    }

}