package com.khairy.captured_photo.model.response

import com.khairy.captured_photo.model.dto.WeatherDto

data class WeatherInfo(
    var main: Main? = null,
    var name: String? = null,
    var sys: Sys? = null,
    var weather: List<Weather>? = null,
)

fun WeatherInfo.toWeatherModel(): WeatherDto {
    return WeatherDto(
        name,
        sys?.country,
        weather?.first()?.main,
        main?.temp,
        main?.tempMax,
        main?.tempMin,
        weather?.first()?.weatherIconURL
    )
}