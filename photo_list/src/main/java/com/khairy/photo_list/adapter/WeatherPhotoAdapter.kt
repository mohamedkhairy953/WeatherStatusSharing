package com.khairy.photo_list.adapter

import com.khairy.core.listeners.OnWeatherPhotoClickListener
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import com.khairy.photo_list.R
import com.khairy.photo_list.databinding.ItemWeatherPhotoBinding
import com.khairy.photo_list.model.dto.WeatherPhotoDto

open class WeatherPhotoAdapter(
    private val weatherPhotosList: List<WeatherPhotoDto>?,
    private val onWeatherPhotoClickListener: OnWeatherPhotoClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: ItemWeatherPhotoBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_weather_photo,
            parent,
            false
        )
        binding.onClickListener = onWeatherPhotoClickListener
        return WeatherPhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val weatherPhoto = weatherPhotosList!![position]
        binding.weatherPhoto = weatherPhoto
    }

    override fun getItemCount(): Int {
        return if (!weatherPhotosList.isNullOrEmpty()) {
            weatherPhotosList.size
        } else {
            0
        }
    }

    protected inner class WeatherPhotoViewHolder internal constructor(binding: ItemWeatherPhotoBinding) :
        RecyclerView.ViewHolder(binding.root)
}