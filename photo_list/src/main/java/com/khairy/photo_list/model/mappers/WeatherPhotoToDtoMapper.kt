package com.khairy.photo_list.model.mappers

import com.khairy.core.data.db.entities.WeatherPhotoEntity
import com.khairy.core.interfaces.Mapper
import com.khairy.photo_list.model.dto.WeatherPhotoDto

class WeatherPhotoToDtoMapper : Mapper<WeatherPhotoEntity, WeatherPhotoDto> {
    override fun map(from: WeatherPhotoEntity): WeatherPhotoDto {
        return WeatherPhotoDto(from.name, from.photoPath)
    }

    override fun mapToList(froms: List<WeatherPhotoEntity>): List<WeatherPhotoDto> {
        return froms.map { map(it) }
    }
}