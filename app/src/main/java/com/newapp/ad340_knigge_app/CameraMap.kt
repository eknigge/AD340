package com.newapp.ad340_knigge_app

import android.Manifest
import android.content.pm.PackageManager

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.newapp.ad340_knigge_app.Model.CameraLocation
import com.newapp.ad340_knigge_app.Model.CameraModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

internal class CameraMap: AppCompatActivity(), OnMapReadyCallback {
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100
    private lateinit var mMap: GoogleMap
    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val SEATTLE = LatLng(47.6081206,-122.3370977)
    private val DEFAULT_ZOOM = 12f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_camera_map)

        // Set action bar title
        supportActionBar?.title = "Traffic Cam Map"

        // check permissions
        getLocationPermission()

        // Construct a FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    fun getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    fun getDeviceLocation(){
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        val lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            // move camera
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), 14f))
                            // add marker
                            mMap?.addMarker(
                                MarkerOptions()
                                    .position(
                                        LatLng(lastKnownLocation.latitude,
                                        lastKnownLocation.longitude))
                                    .title("Current Location")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            )
                        }
                    } else {
                        Log.d("ERROR", "Current location is null. Using defaults.")
                        Log.e("ERROR", "Exception: %s", task.exception)
                        mMap?.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(SEATTLE, DEFAULT_ZOOM))
                        mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12f))

        // Center map in Seattle, re-center when user location available
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEATTLE))

        getCameraData()
    }

    fun getCameraData(){
        val api = ApiObject.getApiObject()!!
        CoroutineScope(Dispatchers.IO).launch{
            val response = api.getTrafficCamData().awaitResponse()
            if(response.isSuccessful){
                val data = response.body()?.get("Features")!!.asJsonArray
                val locationData = apiToList(data)

                withContext(Dispatchers.Main){
                    getDeviceLocation()
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