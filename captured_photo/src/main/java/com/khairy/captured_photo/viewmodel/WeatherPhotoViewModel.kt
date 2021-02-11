package com.khairy.captured_photo.viewmodel

import android.location.Location
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.khairy.captured_photo.model.repository.WeatherPhotoRepository
import com.khairy.captured_photo.model.dto.WeatherDto
import com.khairy.core.base.BaseViewModel
import com.khairy.captured_photo.model.WeatherResponseWrapper
import com.khairy.core.utils.livedata.SingleLiveEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WeatherPhotoViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle?,
    private val repository: WeatherPhotoRepository
) : BaseViewModel() {
    val weatherDataLD: SingleLiveEvent<WeatherDto> = SingleLiveEvent()
    fun setLocation(location: Location) {
        fetchWeatherData(location)
    }

    fun saveWeatherPhoto(photoPath: String) {
        viewModelScope.launch { repository.insertWeatherPhoto(photoPath) }
    }


    private fun fetchWeatherData(location: Location?) {
        dataLoading.value = true
        viewModelScope.launch {
            val resp = repository.getWeatherData(location!!)
            dataLoading.value = false
            when (resp) {
                is WeatherResponseWrapper.Success -> {
                    weatherDataLD.value = resp.data
                }
                is WeatherResponseWrapper.ServerLogicalFailure -> errorMessageEvent.value = resp.message
                WeatherResponseWrapper.NetworkError -> showNoNetworkScreenEvent.value = true
                WeatherResponseWrapper.ServerError -> showServerIssueEvent.value = true
            }
        }
    }
}