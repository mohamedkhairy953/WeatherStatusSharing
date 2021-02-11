package com.khairy.captured_photo.di

import com.khairy.captured_photo.model.repository.WeatherPhotoRepository
import com.khairy.core.data.db.WeatherDatabase
import com.khairy.captured_photo.model.remote.WeatherPhotoWebServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import retrofit2.Retrofit

@Module
@InstallIn(ActivityComponent::class)
class WeatherPhotoModule {
    @Provides
    fun providesWeatherPhotoRepository(
        apiService: WeatherPhotoWebServices, weatherDatabase: WeatherDatabase
    ): WeatherPhotoRepository {
        return WeatherPhotoRepository(apiService, weatherDatabase.weatherPhotoDao())
    }

    @Provides
    fun provideApi(retrofit: Retrofit): WeatherPhotoWebServices {
        return retrofit.create(WeatherPhotoWebServices::class.java)
    }

}