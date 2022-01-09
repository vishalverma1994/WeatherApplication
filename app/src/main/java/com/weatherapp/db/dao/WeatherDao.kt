package com.weatherapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.weatherapp.db.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherEntity: WeatherEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWeather(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather_table")
    fun getAllWeather(): LiveData<List<WeatherEntity>>

    @Query("SELECT * FROM weather_table")
    fun getTestWeather(): List<WeatherEntity>

    @Query("SELECT * FROM weather_table where weatherId = :weatherId")
    fun getWeatherDetail(weatherId: Int): LiveData<WeatherEntity>

    @Delete
    suspend fun deleteWeather(weatherEntity: WeatherEntity)
}