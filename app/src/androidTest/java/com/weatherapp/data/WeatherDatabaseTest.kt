package com.weatherapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.weatherapp.db.WeatherDatabase
import com.weatherapp.db.dao.WeatherDao
import com.weatherapp.db.entity.WeatherEntity
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat
import com.weatherapp.model.*


@RunWith(AndroidJUnit4::class)
class WeatherDatabaseTest : TestCase() {

    private lateinit var weatherDatabase: WeatherDatabase
    private lateinit var weatherDao: WeatherDao

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        weatherDatabase = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java).build()
        weatherDao = weatherDatabase.getWeatherDao()
    }

    @After
    fun closeDatabase() {
        weatherDatabase.close()
    }

    @Test
    fun writeAndReadWeather() = runBlocking {
        val weatherEntity = setWeatherEntity()
        weatherDao.insertWeather(weatherEntity)
        val weatherList = weatherDao.getTestWeather()
        assertThat(weatherList.contains(weatherEntity)).isTrue()
    }

    private fun setWeatherEntity(): WeatherEntity {
        val weatherEntity = WeatherEntity()
        weatherEntity.id = 0
        val coordinates = Coordinates(lon = 30.31, lat = 78.03)
        weatherEntity.coord = coordinates
        val weatherList = listOf(WeatherData(0, "dummyMain", "dummyDesc", "icon"))
        weatherEntity.weather = weatherList
        weatherEntity.base = "dummyBase"
        val mainDetails = MainDetails(313.3, 313.3, 313.3, 313.3, 21, 23, 2323, 243)
        weatherEntity.main = mainDetails
        weatherEntity.visibility = 45
        val windDetails = WindDetails(33.3, 2, 32.1)
        weatherEntity.wind = windDetails
        val cloudDetails = CloudDetails(12)
        weatherEntity.clouds = cloudDetails
        weatherEntity.dt = 10
        val sysDetails = SysDetails("IN", 2333332, 2323232)
        weatherEntity.sys = sysDetails
        weatherEntity.timezone = 12321
        weatherEntity.name = "Dehradun"
        weatherEntity.cod = 34
        weatherEntity.timestamp = System.currentTimeMillis().toString()
        return weatherEntity
    }
}