package com.khairy.captured_photo.model.repository

import android.location.Location
import com.khairy.captured_photo.model.remote.WeatherPhotoWebServices
import com.khairy.core.data.db.entities.WeatherPhotoEntity
import com.khairy.captured_photo.model.WeatherResponseWrapper
import com.khairy.captured_photo.model.response.toWeatherModel
import com.khairy.core.data.db.WeatherPhotoDao
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class WeatherPhotoRepository @Inject constructor(
    private val apiService: WeatherPhotoWebServices,
    private val weatherPhotoDao: WeatherPhotoDao?
) {
    suspend fun getWeatherData(location: Location): WeatherResponseWrapper {
        return try {
            val resp = apiService.getWeatherData(location.latitude, location.longitude)
            WeatherResponseWrapper.Success(resp.toWeatherModel())
        } catch (e: HttpException) {
            WeatherResponseWrapper.ServerLogicalFailure(e.message())
        } catch (e: IOException) {
            WeatherResponseWrapper.NetworkError
        } catch (e: Exception) {
            WeatherResponseWrapper.ServerError
        }
    }


    suspend fun insertWeatherPhoto(photoPath: String) {
        val photoName = getPhotoName(photoPath)
        val weatherPhoto =
            WeatherPhotoEntity(photoName, photoPath)
        weatherPhotoDao!!.insert(weatherPhoto)
    }

    private fun getPhotoName(photoPath: String): String {
        var name = photoPath.replace(IMAGE_FORMAT, "")
        val arr = name.split("_".toRegex()).toTypedArray()
        name = arr[1] + "_" + arr[2]
        return name
    }

    companion object {
        private const val IMAGE_FORMAT = ".jpg"
    }

}