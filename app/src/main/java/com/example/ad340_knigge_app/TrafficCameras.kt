package com.example.ad340_knigge_app

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrafficCameras : AppCompatActivity() {
    val API_URL = "https://web6.seattle.gov/Travelers/api/Map/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traffic_cameras)
        checkNetworkStatus()
    }

    fun getAPIData(recyclerView: RecyclerView){
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CamApiService::class.java)

        val response = retrofit.getTrafficCamData()
        recyclerView.adapter = TrafficAdapter(response)
    }

    fun noConnection() {
        val noConnectionText = findViewById<TextView>(R.id.text_traffic_no_connection)
        noConnectionText.text = (R.string.no_connection.toString())
    }

    fun checkNetworkStatus(){
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        val linkProperties = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if (linkProperties == true) {
            val recyclerViewTraffic = findViewById<RecyclerView>(R.id.recycler_view_traffic)
            getAPIData(recyclerViewTraffic)
        } else {
            noConnection()
        }
    }
}