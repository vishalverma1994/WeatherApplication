package com.weatherapp.repository

import com.weatherapp.api.ApiHelper
import com.weatherapp.db.dao.WeatherDao
import com.weatherapp.db.entity.WeatherEntity
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherDao: WeatherDao, private val apiHelper: ApiHelper) {

    suspend fun insertWeather(weatherEntity: WeatherEntity) = weatherDao.insertWeather(weatherEntity)

    suspend fun deleteWeather(weatherEntity: WeatherEntity) = weatherDao.deleteWeather(weatherEntity)

    fun getAllWeather() = weatherDao.getAllWeather()

    fun getWeatherDetail(weatherId: Int) = weatherDao.getWeatherDetail(weatherId)

    suspend fun fetchWeatherForCurrentLoc(lat: String, lon: String) =
        apiHelper.fetchWeatherForCurrentLoc(lat, lon)

    suspend fun searchForWeather(query: String) = apiHelper.searchForWeather(query)
}