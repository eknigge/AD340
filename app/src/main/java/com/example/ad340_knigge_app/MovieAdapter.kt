package com.example.ad340_knigge_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
        val movieList: Array<Array<String>>,
        val clickListener: (Int) -> Unit
        ): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

        // Item View and place within RecyclerView
        class MovieViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.movie_title)
            val year: TextView = itemView.findViewById(R.id.movie_year)
            var currentMovie: String? = null

        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
       val view = LayoutInflater.from(parent.context)
               .inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }


    // Returns size of data list
    override fun getItemCount(): Int {
        return movieList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val itemValues = movieList[position]
        holder.title.text = itemValues[0]
        holder.year.text = itemValues[1]
        holder.itemView.setOnClickListener{clickListener(position)}
    }

}