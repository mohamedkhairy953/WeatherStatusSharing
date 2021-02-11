package com.khairy.captured_photo.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.FileObserver
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.khairy.captured_photo.R
import com.khairy.captured_photo.databinding.FragmentWeatherPhotoBinding
import com.khairy.captured_photo.viewmodel.WeatherPhotoViewModel
import com.khairy.captured_photo.model.dto.WeatherDto
import com.khairy.core.listeners.OnFragmentInteractionListener
import com.khairy.core.utils.PermissionManager
import com.khairy.navigation_module.PHOTO_EXTRA
import com.khairy.navigation_module.WEATHER_PHOTO_EXTRA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class WeatherPhotoFragment : Fragment(),
    LocationManagerInteraction {
    private val photoPath: String? by lazy { arguments?.getString(PHOTO_EXTRA) }
    private val viewModel: WeatherPhotoViewModel by viewModels()
    private lateinit var binding: FragmentWeatherPhotoBinding
    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null
    private var fileObserver: FileObserver? = null
    private val requestPermissionLauncher: ActivityResultLauncher<String> by lazy { generatePermissionsLauncher() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocationManager()
    }

    private fun generatePermissionsLauncher() =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { bool ->
            if (bool) {
                initLocationManager()
            } else {
                PermissionManager.showApplicationSettingsDialog(context)
            }
        }

    @SuppressLint("MissingPermission")
    private fun initLocationManager() {
        if (isLocationPermissionGranted) {
            locationManager = LocationManager(requireActivity(), this)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun listenToWeatherDataChanges() {
        viewModel.weatherDataLD.observe(viewLifecycleOwner,
            { data: WeatherDto? -> handleWeatherData(data) })

        viewModel.dataLoading.observe(viewLifecycleOwner,
            { if (it) showLoading() else hideLoading() })

        viewModel.errorMessageEvent.observe(viewLifecycleOwner) {
            it?.let { showErrorMessage(it) }
        }
        viewModel.showServerIssueEvent.observe(viewLifecycleOwner) {
            if (it)
                showErrorMessage("Server Error")
        }
        viewModel.showNoNetworkScreenEvent.observe(viewLifecycleOwner) {
            if (it)
                showErrorMessage("Network Error")

        }
    }

    private fun handleWeatherData(weatherDto: WeatherDto?) {
        if (weatherDto != null) {
            lifecycleScope.launch {
                val bitmap = generateWeatherDataOverTheImage(weatherDto)
                photoPath?.let {
                    observeFileChanges(it)
                    val f = replaceOriginalBitmapWithGeneratedBitmap(it, bitmap)
                    viewModel.saveWeatherPhoto(f.absolutePath)
                }
            }

        }
    }

    private fun observeFileChanges(photoPath: String) {
        if (fileObserver == null) {
            Log.d(TAG, "FileObserver Created")
            fileObserver = object : FileObserver(File(photoPath)) {
                override fun onEvent(event: Int, file: String?) {
                    Handler(Looper.getMainLooper()).post {
                        hideLoading()
                        if (event == CLOSE_WRITE) {
                            navigateToShareFragment(photoPath)
                        }
                    }
                }
            }
        }
        fileObserver!!.startWatching() //START OBSERVING

    }

    private fun navigateToShareFragment(photoPath: String?) {
        val bundle = Bundle()
        bundle.putString(WEATHER_PHOTO_EXTRA, photoPath)
        findNavController().navigate(R.id.action_weatherFragment_to_shareFragment, bundle)
    }

    private suspend fun generateWeatherDataOverTheImage(weatherDto: WeatherDto): Bitmap {
        setViews(weatherDto)
        delay(1000) //workaround and not reliable solution
        return convertViewToBitmap(binding.weatherPhotoLayout)
    }

    @SuppressLint("SetTextI18n")
    private fun setViews(weatherDto: WeatherDto) {
        binding.locationNameTv.text =
            String.format("%s, %s", weatherDto.name, weatherDto.countryName)
        binding.tempStatusTv.text = weatherDto.tempStatus
        binding.tempTv.text = String.format("%s°", weatherDto.temp)
        binding.minMaxTv.text = String.format(
            "%s ° / %s °",
            weatherDto.maxTemp,
            weatherDto.minTemp
        )
        Glide.with(requireContext()).load(weatherDto.tempIconURL).into(binding.temperatureStatusIv)
        setOverlayVisible(true)
    }

    private suspend fun convertViewToBitmap(v: ConstraintLayout): Bitmap {
        val b = Bitmap.createBitmap(
            v.width,
            v.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }

    private fun replaceOriginalBitmapWithGeneratedBitmap(
        photoPath: String,
        capturedImageBitmap: Bitmap
    ): File {
        val newModifiedCapturedImage = File(photoPath)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(newModifiedCapturedImage)
            capturedImageBitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.message ?: "FileNotFoundException")
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush()
                    outputStream.fd.sync()
                    outputStream.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.message ?: "IOException")
                }
            }
        }
        return newModifiedCapturedImage
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_weather_photo, container, false)
        listenToWeatherDataChanges()
        onClickActions()
        return binding.root
    }

    private fun onClickActions() {
        binding.generateWeatherDataBtn.setOnClickListener {
            onGenerateWeatherDataClicked()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoPath?.let {
            Glide.with(requireContext()).load(File(it)).into(binding.weatherPhotoIv)
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as OnFragmentInteractionListener).setToolbarTitle(getString(R.string.new_photo))
        setOverlayVisible(currentLocation != null)
    }


    private val isLocationPermissionGranted: Boolean
        get() = PermissionManager.isPermissionGranted(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    override fun onPause() {
        super.onPause()
        if (locationManager != null) {
            locationManager!!.stopLocationUpdates()
        }
        if (fileObserver != null) {
            fileObserver!!.stopWatching()
        }
    }

    private fun onGenerateWeatherDataClicked() {
        if (isLocationPermissionGranted) {
            showLoading()
            locationManager!!.startLocationUpdates()
        } else {
            initLocationManager()
        }
    }

    override fun onLocationRetrieved(location: Location) {
        currentLocation = location
        // we don't need more location update. so we should stop it.
        locationManager!!.stopLocationUpdates()
        // get location data from server by lat/long
        viewModel.setLocation(location)
    }

    private fun setOverlayVisible(visible: Boolean) {
        binding.overlay.visibility =
            if (visible) View.VISIBLE else View.GONE
    }

    private fun showLoading() {
        binding.loadingProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loadingProgressBar.visibility = View.GONE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LocationManager.REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d(TAG, "User agreed to make required location settings changes.")
                    if (locationManager != null) locationManager!!.startLocationUpdates()
                }
                Activity.RESULT_CANCELED -> {
                    Log.d(TAG, "User chose not to make required location settings changes.")
                    hideLoading()
                }
                else -> {
                }
            }
        }
    }

    companion object {
        private val TAG = WeatherPhotoFragment::class.java.simpleName
    }
}