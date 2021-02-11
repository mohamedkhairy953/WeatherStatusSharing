package com.khairy.captured_photo.model.dto

data class WeatherDto(
    var name: String? = null,
    var countryName: String? = null,
    var tempStatus: String? = null,
    var temp: Double? = 0.0,
    var maxTemp: Double? = 0.0,
    var minTemp: Double? = 0.0,
    var tempIconURL: String? = null,
)

