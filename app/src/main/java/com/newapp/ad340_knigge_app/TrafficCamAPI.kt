package com.newapp.ad340_knigge_app

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET

interface CamApiService {
    @GET("Data?zoomId=13&type=2")
    fun getTrafficCamData(): Call<JsonObject>
}