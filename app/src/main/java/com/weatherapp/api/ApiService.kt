package com.weatherapp.api

import com.weatherapp.db.entity.WeatherEntity
import com.weatherapp.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(Constants.FETCH_WEATHER_CURRENT_LOCATION)
    suspend fun fetchWeatherForCurrentLoc(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appId: String
    ): Response<WeatherEntity>

    @GET(Constants.FETCH_WEATHER_CURRENT_LOCATION)
    suspend fun searchForWeather(
        @Query("q") query: String,
        @Query("appid") appId: String
    ): Response<WeatherEntity>
}