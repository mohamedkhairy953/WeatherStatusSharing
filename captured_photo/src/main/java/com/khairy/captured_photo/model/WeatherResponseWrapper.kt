package com.khairy.captured_photo.model

import com.khairy.captured_photo.model.dto.WeatherDto

sealed class WeatherResponseWrapper {
    class Success(val data: WeatherDto) : WeatherResponseWrapper()
    class ServerLogicalFailure(val message: String?) : WeatherResponseWrapper()
    object NetworkError : WeatherResponseWrapper()
    object ServerError : WeatherResponseWrapper()
}