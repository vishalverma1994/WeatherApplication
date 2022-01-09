package com.weatherapp.services

import android.Manifest.permission
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.Builder
import androidx.core.app.ActivityCompat
import com.weatherapp.utils.Constants

class GpsTracker(private val mContext: Context) : Service(), LocationListener {

    // flag for GPS status
    private var isGPSEnabled = false

    // flag for network status
    private var isNetworkEnabled = false

    // flag for GPS status
    private var canGetLocation = false

    private lateinit var location: Location
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    // Declaring a Location Manager
    private lateinit var locationManager: LocationManager

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1 // 1 minute
    }

    init {
        getLocation()
    }

    private fun getLocation(): Location? {
        try {
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                canGetLocation = true
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    //check the network permission
                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(
                            mContext,
                            permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            (mContext as Activity),
                            arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION),
                            Constants.REQUEST_CODE_LOCATION_PERMISSION
                        )
                    }
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("Network", "Network")
                    if (::locationManager.isInitialized) {
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let {
                            location = it
                        }
                        if (::location.isInitialized) {
                            latitude = location.latitude
                            longitude = location.longitude
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (!::location.isInitialized) {
                        //check the network permission
                        if (ActivityCompat.checkSelfPermission(
                                mContext,
                                permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(
                                mContext,
                                permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                (mContext as Activity),
                                arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION),
                                Constants.REQUEST_CODE_LOCATION_PERMISSION
                            )
                        }
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (::locationManager.isInitialized) {
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                                location = it
                            }
                            if (::location.isInitialized) {
                                latitude = location.latitude
                                longitude = location.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (::location.isInitialized)
            location
        else null
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (::locationManager.isInitialized) {
            locationManager.removeUpdates(this@GpsTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (::location.isInitialized) {
            latitude = location.latitude
        }

        // return latitude
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (::location.isInitialized) {
            longitude = location.longitude
        }

        // return longitude
        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    override fun onLocationChanged(location: Location) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }
}