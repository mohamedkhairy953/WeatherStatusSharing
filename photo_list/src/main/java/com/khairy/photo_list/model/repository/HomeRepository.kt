package com.khairy.photo_list.model.repository

import com.khairy.core.data.db.WeatherPhotoDao
import com.khairy.core.data.db.entities.WeatherPhotoEntity
import com.khairy.core.interfaces.Mapper
import com.khairy.photo_list.model.dto.WeatherPhotoDto
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val weatherDataDao: WeatherPhotoDao,
    private val mapper: Mapper<WeatherPhotoEntity, WeatherPhotoDto>
) {
    suspend fun getWeatherPhotos() = mapper.mapToList(weatherDataDao.weatherPhotoList())
}