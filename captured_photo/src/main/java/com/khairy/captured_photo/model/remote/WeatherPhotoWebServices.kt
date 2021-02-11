package com.khairy.captured_photo.model.remote

import retrofit2.http.GET
import com.khairy.captured_photo.model.response.WeatherInfo
import retrofit2.http.Query

interface WeatherPhotoWebServices {
    @GET("data/2.5/weather")
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): WeatherInfo
}