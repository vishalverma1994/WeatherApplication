package com.weatherapp.contentprovider

import android.content.*
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.weatherapp.db.WeatherDatabase
import com.weatherapp.db.dao.WeatherDao
import com.weatherapp.db.entity.WeatherEntity
import com.weatherapp.utils.Constants
import javax.inject.Inject

class WeatherContentProvider : ContentProvider() {

    @Inject
    lateinit var weatherDatabase: WeatherDatabase

    companion object {
        /**The match code for some items in the weather table.  */
        private const val CODE_WEATHER_DIR = 1

        /** The match code for an item in the weather table.  */
        private const val CODE_WEATHER_ITEM = 2

        /** The URI matcher.  */
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        init {
            MATCHER.addURI(
                Constants.AUTHORITY,
                "weather",
                CODE_WEATHER_DIR
            )
            MATCHER.addURI(
                Constants.AUTHORITY,
                "weather/*",
                CODE_WEATHER_ITEM
            )
        }
    }

    override fun onCreate(): Boolean {
        context?.let {
            weatherDatabase = Room.inMemoryDatabaseBuilder(it, WeatherDatabase::class.java).build()
        }
        return true
    }

    override fun query(uri: Uri, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor? {
        val code = MATCHER.match(uri)
        return if (code == CODE_WEATHER_DIR || code == CODE_WEATHER_ITEM) {
            val context = context ?: return null
            if (::weatherDatabase.isInitialized)
                weatherDatabase = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java).build()
            val weatherDao: WeatherDao = weatherDatabase.getWeatherDao()
            val cursor: Cursor = if (code == CODE_WEATHER_DIR) {
                weatherDao.selectAll()
            } else {
                weatherDao.selectById(ContentUris.parseId(uri).toInt())
            }
            cursor.setNotificationUri(context.contentResolver, uri)
            cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (MATCHER.match(uri)) {
            CODE_WEATHER_DIR -> "vnd.android.cursor.dir/${Constants.AUTHORITY}.weather"
            CODE_WEATHER_ITEM -> "vnd.android.cursor.item/${Constants.AUTHORITY}.weather"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (MATCHER.match(uri)) {
            CODE_WEATHER_DIR -> {
                val context = context ?: return null
                if (::weatherDatabase.isInitialized)
                    weatherDatabase = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java).build()
                val weatherDao: WeatherDao = weatherDatabase.getWeatherDao()
                values?.let {
                    val id: Long = weatherDao.insertWeatherthroughCP(WeatherEntity.fromContentValues(it))
                    context.contentResolver.notifyChange(uri, null)
                    ContentUris.withAppendedId(uri, id)
                }
            }
            CODE_WEATHER_ITEM -> throw java.lang.IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw java.lang.IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (MATCHER.match(uri)) {
            CODE_WEATHER_DIR -> {
                val context = context ?: return 0
                if (::weatherDatabase.isInitialized)
                    weatherDatabase = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java).build()
                val weatherDao: WeatherDao = weatherDatabase.getWeatherDao()
                val count: Int = weatherDao.deleteWeatherById(ContentUris.parseId(uri))
                context.contentResolver.notifyChange(uri, null)
                count
            }
            else -> throw java.lang.IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (MATCHER.match(uri)) {
            CODE_WEATHER_ITEM -> throw java.lang.IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            CODE_WEATHER_DIR -> {
                val context = context ?: return 0
                if (::weatherDatabase.isInitialized)
                    weatherDatabase = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java).build()
                val weatherDao: WeatherDao = weatherDatabase.getWeatherDao()
                val weatherEntity: WeatherEntity = WeatherEntity.fromContentValues(values)
                val count: Int = weatherDao.updateWeather(weatherEntity)
                context.contentResolver.notifyChange(uri, null)
                count
            }
            else -> throw java.lang.IllegalArgumentException("Unknown URI: $uri")
        }
    }
}