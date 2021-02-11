package com.khairy.photo_list.di

import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import com.khairy.core.data.db.WeatherDatabase
import com.khairy.core.data.db.entities.WeatherPhotoEntity
import com.khairy.core.interfaces.Mapper
import com.khairy.photo_list.model.mappers.WeatherPhotoToDtoMapper
import com.khairy.photo_list.model.dto.WeatherPhotoDto
import com.khairy.photo_list.model.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
@InstallIn(ActivityComponent::class)
class HomeModule {
    @Provides
    fun providesHomeRepository(
        weatherDatabase: WeatherDatabase,
        mapper: WeatherPhotoToDtoMapper
    ): HomeRepository {
        return HomeRepository(weatherDatabase.weatherPhotoDao(), mapper)
    }

    @Provides
    fun providesMapper(): WeatherPhotoToDtoMapper {
        return WeatherPhotoToDtoMapper()
    }
}