package com.weatherapp.viewmodel

import androidx.lifecycle.*
import com.weatherapp.db.entity.WeatherEntity
import com.weatherapp.repository.WeatherRepository
import com.weatherapp.utils.Constants
import com.weatherapp.utils.NetworkHelper
import com.weatherapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _finalWeatherLiveData = MediatorLiveData<List<WeatherEntity>>()

    val finalWeatherLiveData: LiveData<List<WeatherEntity>> get() = _finalWeatherLiveData

    private val _weatherLiveData = MutableLiveData<Resource<WeatherEntity>>()

    val weatherLiveData: LiveData<Resource<WeatherEntity>> get() = _weatherLiveData

    fun fetchWeatherDetails(lat: String, lon: String) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()) {
            repository.fetchWeatherForCurrentLoc(lat, lon).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { weatherEntity ->
                        weatherEntity.timestamp = System.currentTimeMillis().toString()
                        repository.insertWeather(weatherEntity)
                    }
                } else {
                    _weatherLiveData.postValue(Resource.error(response.errorBody().toString(), null))
                }
            }
        } else {
            _weatherLiveData.postValue(Resource.error(Constants.INTERNET_CONNECTION_ERROR_MESSAGE, null))
        }
    }

    fun searchForWeather(query: String) = viewModelScope.launch {
        _weatherLiveData.postValue(Resource.loading(null))
        if (networkHelper.isNetworkConnected()) {
            repository.searchForWeather(query).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { weatherEntity ->
                        weatherEntity.timestamp = System.currentTimeMillis().toString()
                        repository.insertWeather(weatherEntity)
                        _weatherLiveData.postValue(Resource.success(weatherEntity))
                    }
                } else {
                    _weatherLiveData.postValue(Resource.error(response.errorBody().toString(), null))
                }
            }
        }
    }

    //fetch all weather list from database
    fun getAllWeatherList() {
        _finalWeatherLiveData.addSource(repository.getAllWeather()) {
            _finalWeatherLiveData.postValue(it)
        }
    }

    fun getWeatherDetail(weatherId: Int): LiveData<WeatherEntity> {
        return repository.getWeatherDetail(weatherId)
    }

    fun deleteWeather(weatherEntity: WeatherEntity) = viewModelScope.launch {
        repository.deleteWeather(weatherEntity)
    }
}