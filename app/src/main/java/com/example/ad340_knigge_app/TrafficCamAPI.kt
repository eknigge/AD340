package com.example.ad340_knigge_app

import com.example.ad340_knigge_app.Model.Camera
import com.example.ad340_knigge_app.Model.CameraModel
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CamApiService {
    @GET("Data?zoomId=13&type=2")
    fun getTrafficCamData(): Call<JsonObject>
}