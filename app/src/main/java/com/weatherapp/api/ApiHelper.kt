package com.weatherapp.api

import com.weatherapp.db.entity.WeatherEntity
import retrofit2.Response

interface ApiHelper {
    suspend fun fetchWeatherForCurrentLoc(lat: String, lon: String): Response<WeatherEntity>

    suspend fun searchForWeather(query: String): Response<WeatherEntity>
}