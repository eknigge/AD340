package com.example.ad340_knigge_app

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ad340_knigge_app.Model.CameraLocation
import com.example.ad340_knigge_app.Model.CameraModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import javax.security.auth.callback.Callback
import kotlin.reflect.typeOf

internal class CameraMap: AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_camera_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12f))

        // Add a marker in Sydney and move the camera
        val seattle = LatLng(47.6081206,-122.3370977)
        mMap.addMarker(MarkerOptions()
                .position(seattle)
                .title("Marker on Seattle"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seattle))

        getLocationData()

    }

    fun getLocationData(){
        val api = ApiObject.getApiObject()!!
        CoroutineScope(Dispatchers.IO).launch{
            val response = api.getTrafficCamData()
            val data = response.execute().body()?.get("Features")!!.asJsonArray
            val locationData = apiToList(data)
            Log.d("DATA", locationData.toString())
        }
    }

    fun apiToList(jsonArray: JsonArray): ArrayList<Double> {
        val cameraList = ArrayList<CameraModel>()
        val cameraLocation = ArrayList<Double>()
        val n = jsonArray.size()-1

        for(i in 0..n){
            val item = jsonArray[i].asJsonObject
            val cameraData = item.getAsJsonArray("Cameras")[0].asJsonObject
            val locationData = item.getAsJsonArray("PointCoordinate").asJsonArray

            // add location data
            cameraLocation.add(locationData[0].asDouble)
            cameraLocation.add(locationData[1].asDouble)

            // add camera data
            cameraList.add(
                CameraModel(
                    cameraData.get("Id").asString,
                    cameraData.get("Description").asString,
                    cameraData.get("ImageUrl").asString,
                    cameraData.get("Type").asString,)
            )
        }
        return cameraLocation
    }

//    private fun internetAvailable(): Boolean? {
//        val connectivity = getSystemService(ConnectivityManager::class.java)
//        val currentConnection = connectivity.activeNetwork
//        val caps = connectivity.getNetworkCapabilities(currentConnection)
//        return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//    }
}