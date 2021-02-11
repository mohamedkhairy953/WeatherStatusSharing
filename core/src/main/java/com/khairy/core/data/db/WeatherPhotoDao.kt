package com.khairy.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.khairy.core.data.db.entities.WeatherPhotoEntity

@Dao
interface WeatherPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherPhoto:WeatherPhotoEntity)

    @Query("SELECT * FROM weatherPhoto")
    suspend fun weatherPhotoList(): List<WeatherPhotoEntity>
}