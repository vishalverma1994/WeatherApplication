package com.weatherapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.weatherapp.db.converter.ListTypeConverter
import com.weatherapp.db.dao.WeatherDao
import com.weatherapp.db.entity.WeatherEntity
import com.weatherapp.model.WeatherData

@Database(
    entities = [WeatherEntity::class, WeatherData::class],
    version = 1
)
@TypeConverters(ListTypeConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao
}