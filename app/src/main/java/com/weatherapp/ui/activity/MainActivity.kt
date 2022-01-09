package com.weatherapp.ui.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.weatherapp.R
import com.weatherapp.databinding.ActivityMainBinding
import com.weatherapp.services.GpsTracker
import com.weatherapp.utils.CheckPermissions
import com.weatherapp.utils.Constants
import com.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var gpsTracker: GpsTracker

    companion object {
        private val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
    }

    //initializing activity components
    private fun initComponents() {
        setSupportActionBar(binding.toolbar)
        requestPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::gpsTracker.isInitialized)
            gpsTracker.stopUsingGPS()
    }

    //Checking for location permission
    private fun requestPermissions() {
        if (CheckPermissions.hasLocationPermissions(this)) {
            fetchLocation()
            return
        }
        //asking for permissions if not allowed
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.location_permission_message),
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.location_permission_message),
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //permission granted
        fetchLocation()
    }

    //Fetching the location and saving weather based on that location in database
    private fun fetchLocation() {
        gpsTracker = GpsTracker(this)
        if (gpsTracker.canGetLocation()) {
            Log.e(TAG, "Lat : ${gpsTracker.getLatitude()} Long : ${gpsTracker.getLongitude()}")
            viewModel.fetchWeatherDetails(gpsTracker.getLatitude().toString(), gpsTracker.getLongitude().toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}