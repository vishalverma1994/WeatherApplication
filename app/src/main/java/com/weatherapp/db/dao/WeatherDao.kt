package com.weatherapp.db.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.weatherapp.db.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherEntity: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherthroughCP(weatherEntity: WeatherEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWeather(weatherEntity: WeatherEntity): Int

    @Query("SELECT * FROM weather_table")
    fun getAllWeather(): LiveData<List<WeatherEntity>>

    @Query("SELECT * FROM weather_table")
    fun getTestWeather(): List<WeatherEntity>

    @Query("SELECT * FROM weather_table where weatherId = :weatherId")
    fun getWeatherDetail(weatherId: Int): LiveData<WeatherEntity>

    @Query("SELECT * FROM weather_table")
    fun selectAll(): Cursor

    @Query("SELECT * FROM weather_table where weatherId = :weatherId")
    fun selectById(weatherId: Int): Cursor

    @Delete
    suspend fun deleteWeather(weatherEntity: WeatherEntity)

    @Query("DELETE FROM weather_table WHERE weatherId = :id")
    fun deleteWeatherById(id: Long): Int
}