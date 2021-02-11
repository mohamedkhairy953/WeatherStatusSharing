package com.khairy.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import kotlin.jvm.Synchronized
import androidx.room.Room
import com.khairy.core.data.db.entities.WeatherPhotoEntity

@Database(entities = [WeatherPhotoEntity::class], version = 2, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherPhotoDao(): WeatherPhotoDao

    companion object {
        private const val DATA_BASE_NAME = "weather_db"
        @JvmStatic
        @Synchronized
        fun getDatabaseInstance(context: Context): WeatherDatabase {
            return  Room.databaseBuilder(
                context.applicationContext,  // fallbackToDestructiveMigration is a migration strategy that destroy and re-creating existing db
                // fallbackToDestructiveMigration is only used for small applications like we are implementing now
                // for real projects we need to implement non-destructive migration strategy.
                WeatherDatabase::class.java, DATA_BASE_NAME
            ).fallbackToDestructiveMigration().build()
        }
    }
}