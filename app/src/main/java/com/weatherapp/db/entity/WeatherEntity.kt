package com.weatherapp.db.entity

import android.content.ContentValues
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

    companion object {
        fun fromContentValues(values: ContentValues?): WeatherEntity {
            val weatherEntity = WeatherEntity()
            if (values != null && values.containsKey("weatherId")) {
                weatherEntity.weatherId = values.getAsInteger("weatherId")

            }
            if (values != null && values.containsKey("name")) {
                weatherEntity.name = values.getAsString("name")
            }

            if (values != null && values.containsKey("id")) {
                weatherEntity.id = values.getAsInteger("id")
            }

            if (values != null && values.containsKey("base")) {
                weatherEntity.base = values.getAsString("base")
            }

            if (values != null && values.containsKey("dt")) {
                weatherEntity.dt = values.getAsInteger("dt")
            }

            if (values != null && values.containsKey("timezone")) {
                weatherEntity.timezone = values.getAsInteger("timezone")
            }

            if (values != null && values.containsKey("cod")) {
                weatherEntity.cod = values.getAsInteger("cod")
            }

            if (values != null && values.containsKey("timestamp")) {
                weatherEntity.timestamp = values.getAsString("timestamp")
            }
            return weatherEntity
        }
    }
}
