package com.weatherapp.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.weatherapp.model.WeatherData
import java.util.ArrayList

class ListTypeConverter {

    @TypeConverter
    fun fromDataSetList(value: String): List<WeatherData> {
        val listType = object : TypeToken<ArrayList<WeatherData>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDataSetList(list: List<WeatherData>): String {
        return Gson().toJson(list)
    }
}