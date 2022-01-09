package com.weatherapp.api

import com.weatherapp.db.entity.WeatherEntity
import com.weatherapp.utils.Constants
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService
) : ApiHelper {
    override suspend fun fetchWeatherForCurrentLoc(lat: String, lon: String): Response<WeatherEntity> =
        apiService.fetchWeatherForCurrentLoc(lat, lon, Constants.API_KEY)

    override suspend fun searchForWeather(query: String): Response<WeatherEntity>  =
        apiService.searchForWeather(query, Constants.API_KEY)
}