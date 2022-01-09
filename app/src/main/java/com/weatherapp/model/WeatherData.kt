package com.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.weatherapp.db.converter.ListTypeConverter

@Entity
@TypeConverters(ListTypeConverter::class)
data class WeatherData(
    @PrimaryKey
    var id: Int = 0,
    var main: String = "",
    var description: String = "",
    var icon: String = ""
)
