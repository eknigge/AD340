package com.example.ad340_knigge_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ad340_knigge_app.Model.Camera
import com.example.ad340_knigge_app.Model.CameraModel
import retrofit2.Call

class TrafficAdapter(
    val apiResponse: List <CameraModel>
): RecyclerView.Adapter<TrafficAdapter.TrafficViewHolder>(){
    val IMG_URL_BASE = "https://www.seattle.gov/trafficcams/images/"

    // Item View and place within RecyclerView
    class TrafficViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.traffic_cam_title)
        val img: ImageView = itemView.findViewById(R.id.traffic_cam_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrafficViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return TrafficViewHolder(view)
    }


    // Returns size of data list
    override fun getItemCount(): Int {
        //placeholder value
        return 0;
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: TrafficViewHolder, position: Int) {
        holder.title.text = apiResponse[position].Description

        //need to implement showing images
        val ImgUrl = IMG_URL_BASE + apiResponse[position].ImageUrl

    }



}
