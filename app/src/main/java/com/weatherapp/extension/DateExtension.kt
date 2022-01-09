package com.weatherapp.extension

import java.text.SimpleDateFormat
import java.util.*


fun Long.getTimeInUtcFormat(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(this))
}