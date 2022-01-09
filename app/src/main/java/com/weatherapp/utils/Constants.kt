package com.weatherapp.utils

import android.net.Uri

object Constants {

    const val WEATHER_DATABASE_NAME = "weather_db"

    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    /** The authority for content provider.  */
    const val AUTHORITY = "com.weatherapp.contentprovider.WeatherContentProvider"

    /** The URI for the weather table.  */
    val URI_MENU = Uri.parse(
        "content://$AUTHORITY/weather"
    )

    const val INTERNET_CONNECTION_ERROR_MESSAGE = "Please check your internet connection."

    const val API_KEY = "9c54c4d10098b5a74bf8e9cc1a92e2a7"

    const val BASE_URL = "https://api.openweathermap.org/"

    const val FETCH_WEATHER_CURRENT_LOCATION = "data/2.5/weather"

    const val WEATHER_ID = "weatherId"
}