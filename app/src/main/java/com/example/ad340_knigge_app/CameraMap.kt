package com.example.ad340_knigge_app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ad340_knigge_app.Model.Camera
import com.example.ad340_knigge_app.Model.CameraLocation
import com.example.ad340_knigge_app.Model.CameraModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.await
import retrofit2.awaitResponse
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

    fun getLastKnownLocation(){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d("DATA", "Step 1")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("DATA", "Step 2")
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                Log.d("DATA", "Step 3")
                if(location != null){
                    Log.d("DATA", "Step 4")
                    Log.d("DATA", location.toString())
                    centerMapOnUser(location)
                }
            }
            return
        }
    }

    fun centerMapOnUser(location: Location){
       mMap.addMarker(
           MarkerOptions()
               .position(LatLng(location.latitude, location.longitude))
               .title("My Current Location")
               .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
       )

        mMap.moveCamera(CameraUpdateFactory.zoomTo(12f))
        mMap.moveCamera(
            CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude))
        )
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
            val response = api.getTrafficCamData().awaitResponse()
            if(response.isSuccessful){
                val data = response.body()?.get("Features")!!.asJsonArray
                val locationData = apiToList(data)

                withContext(Dispatchers.Main){
                    getLastKnownLocation()
                    updateMap(locationData)
                }
            }
        }
    }

    fun updateMap(data: ArrayList<CameraLocation>){
        mMap.clear()
        for(item: CameraLocation in data){
            val location = LatLng(item.Coordinates[0], item.Coordinates[1])
            mMap.addMarker(
                MarkerOptions().position(location).
                        title(item.Cameras[0].Description)
            )
        }
    }

    fun apiToList(jsonArray: JsonArray): ArrayList<CameraLocation> {
        val cameraList = ArrayList<CameraModel>()
        val cameraLocation = ArrayList<ArrayList<Double>>()
        val cameraArray = ArrayList<CameraLocation>()
        val n = jsonArray.size()-1

        for(i in 0..n){
            val item = jsonArray[i].asJsonObject
            val cameraData = item.getAsJsonArray("Cameras")[0].asJsonObject
            val locationData = item.getAsJsonArray("PointCoordinate").asJsonArray

            // add location data
            val pointContainer = ArrayList<Double>()
            pointContainer.add(locationData[0].asDouble)
            pointContainer.add(locationData[1].asDouble)

            // add camera data
            cameraList.add(
                CameraModel(
                    cameraData.get("Id").asString,
                    cameraData.get("Description").asString,
                    cameraData.get("ImageUrl").asString,
                    cameraData.get("Type").asString,)
            )

            // add to Camera Array
            val singleLocation = CameraLocation(pointContainer, cameraList)
            cameraArray.add(singleLocation)
        }
        return cameraArray
    }

}