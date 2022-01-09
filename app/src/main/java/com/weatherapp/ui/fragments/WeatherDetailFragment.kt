package com.weatherapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.weatherapp.R
import com.weatherapp.databinding.FragmentWeatherDetailBinding
import com.weatherapp.db.entity.WeatherEntity
import com.weatherapp.extension.getTimeInUtcFormat
import com.weatherapp.utils.Constants
import com.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherDetailFragment : Fragment() {

    private val weatherViewModel: WeatherViewModel by activityViewModels()

    private lateinit var binding: FragmentWeatherDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWeatherDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extractArguments()
    }

    //getting Weather Details from weatherId getting from arguments
    private fun extractArguments() {
        fetchWeatherDetail(requireArguments().getInt(Constants.WEATHER_ID))
    }

    //get the weatherEntity object from database
    private fun fetchWeatherDetail(weatherId: Int) {
        weatherViewModel.getWeatherDetail(weatherId).observe(this, { weatherEntity ->
            weatherEntity?.let {
                updateUI(it)
            }
        })
    }

    //updating the data
    private fun updateUI(weatherEntity: WeatherEntity) {
        binding.tvName.text = weatherEntity.name.orEmpty()

        binding.tvTemperature.text =
            getString(R.string.temp, weatherEntity.main?.temp.toString())

        weatherEntity.timestamp?.toLong()?.let { date ->
            binding.tvDate.text = getString(R.string.date, date.getTimeInUtcFormat())
        }

        if (!weatherEntity.weather.isNullOrEmpty())
            binding.tvDesc.text = getString(R.string.desc, weatherEntity.weather!![0].description)

        binding.tvWindSpeed.text = getString(R.string.wind_speed, weatherEntity.wind?.speed.toString())

        binding.tvCountry.text = getString(R.string.country, weatherEntity.sys?.country)
    }

}