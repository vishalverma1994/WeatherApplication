package com.weatherapp.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weatherapp.model.*

@Entity(tableName = "weather_table")
data class WeatherEntity(
    var id: Int? = 0,
    @Embedded
    var coord: Coordinates? = null,
    var weather: List<WeatherData>? = null,
    var base: String? = "",
    @Embedded
    var main: MainDetails? = null,
    var visibility: Int? = 0,
    @Embedded
    var wind: WindDetails? = null,
    @Embedded
    var clouds: CloudDetails? = null,
    var dt: Int? = 0,
    @Embedded
    var sys: SysDetails? = null,
    var timezone: Int? = 0,
    var name:String? = "",
    var cod: Int? = 0,
    var timestamp: String? = ""
) {
    @PrimaryKey(autoGenerate = true)
    var weatherId: Int = 0
}
