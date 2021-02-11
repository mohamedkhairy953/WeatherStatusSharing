package com.khairy.photo_list.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.khairy.photo_list.model.repository.HomeRepository
import androidx.lifecycle.viewModelScope
import com.khairy.core.base.BaseViewModel
import com.khairy.photo_list.model.dto.WeatherPhotoDto
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject
constructor(
    @Assisted savedStateHandle: SavedStateHandle?,
    private val repository: HomeRepository
) : BaseViewModel() {
    val weatherPhotoList: MutableLiveData<List<WeatherPhotoDto>> = MutableLiveData()

    init {
        dataLoading.value = true
        viewModelScope.launch {
            try {
                weatherPhotoList.value = repository.getWeatherPhotos()
            } catch (e: Exception) {
                errorMessageEvent.value = e.message
            }finally {
                dataLoading.value=false
            }

        }
    }
}