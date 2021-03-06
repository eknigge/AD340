package com.newapp.ad340_knigge_app

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newapp.ad340_knigge_app.Model.CameraModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrafficCameras : AppCompatActivity() {
    val API_URL = "https://web6.seattle.gov/Travelers/api/Map/"
    var cameraList = ArrayList<CameraModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traffic_cameras)
        checkNetworkStatus()

        // Set action bar title
        supportActionBar?.title = "Traffic Cameras"

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun getAPIData(recyclerView: RecyclerView){
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CamApiService::class.java)

        val request = retrofit.getTrafficCamData()
        request.enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val data = response.body()?.get("Features")!!.asJsonArray
                cameraList = apiToList(data)
                recyclerView.adapter = TrafficAdapter(cameraList)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable){
                Log.d("Error", "Failed API Response")
                val noConnectionText = findViewById<TextView>(R.id.text_traffic_no_connection)
                noConnectionText.text = ("No Connection. Please check your internet connection and try again")
            }
        })


    }

    fun apiToList(jsonArray: JsonArray): ArrayList<CameraModel> {
        val cameraList = ArrayList<CameraModel>()
        val n = jsonArray.size()-1

        for(i in 0..n){
            val item = jsonArray[i].asJsonObject
            val cameraData = item.getAsJsonArray("Cameras")[0].asJsonObject
            cameraList.add(
                CameraModel(
                    cameraData.get("Id").asString,
                    cameraData.get("Description").asString,
                    cameraData.get("ImageUrl").asString,
                    cameraData.get("Type").asString,)
            )
        }
        return cameraList
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