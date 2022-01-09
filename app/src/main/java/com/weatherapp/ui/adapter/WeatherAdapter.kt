package com.weatherapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weatherapp.R
import com.weatherapp.databinding.AdapterWeatherBinding
import com.weatherapp.db.entity.WeatherEntity
import com.weatherapp.extension.getTimeInUtcFormat

class WeatherAdapter(private val onWeatherSelected: (Int) -> Unit) :
    ListAdapter<WeatherEntity, WeatherAdapter.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterWeatherBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(getItem(position))
    }

    inner class ViewHolder(private val binding: AdapterWeatherBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cvWeather.setOnClickListener {
                onWeatherSelected(absoluteAdapterPosition)
            }
        }

        fun bindViews(weatherEntity: WeatherEntity) {
            //show temperature
            binding.tvTemperature.text =
                context.getString(R.string.temp, weatherEntity.main?.temp.toString())

            //show date
            weatherEntity.timestamp?.toLong()?.let { date ->
                binding.tvDate.text = context.getString(R.string.date, date.getTimeInUtcFormat())
            }
        }

    }
}

class MyDiffUtil : DiffUtil.ItemCallback<WeatherEntity>() {
    override fun areItemsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
        return oldItem == newItem
    }
}