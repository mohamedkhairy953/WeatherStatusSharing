package com.khairy.photo_list.view

import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.khairy.core.listeners.OnWeatherPhotoClickListener
import com.khairy.core.utils.PermissionManager
import com.khairy.core.utils.enumerateCameras
import com.khairy.navigation_module.PIXEL_FORMAT
import com.khairy.navigation_module.CAMERA_ID
import com.khairy.navigation_module.WEATHER_PHOTO_EXTRA
import com.khairy.photo_list.R
import com.khairy.photo_list.adapter.WeatherPhotoAdapter
import com.khairy.photo_list.databinding.FragmentHomeBinding
import com.khairy.photo_list.model.dto.WeatherPhotoDto
import com.khairy.photo_list.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), OnWeatherPhotoClickListener {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            generatePermissionsLauncher()
    }

    private fun generatePermissionsLauncher() =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.entries.none { it.value == false }) {
                onNewPhotoClicked()
            } else {
                PermissionManager.showApplicationSettingsDialog(context)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        observeWeatherPhotoChanges()
        onClickActions()
        return binding.root
    }

    private fun onClickActions() {
        binding.newWeatherPhotoBtn.setOnClickListener {
            onNewPhotoClicked()
        }
    }

    private fun observeWeatherPhotoChanges() {
        viewModel.dataLoading.observe(viewLifecycleOwner, {
            if (it) showLoadingLayout() else hideLoadingLayout()
        })
        viewModel.errorMessageEvent.observe(viewLifecycleOwner, {
            it?.let { handleWeatherPhotoListError(it) }
        })

        viewModel.weatherPhotoList.observe(viewLifecycleOwner, { weatherPhotos ->
            if (weatherPhotos.isNotEmpty()) {
                setupRecycler(weatherPhotos)
            } else {
                showEmptyLayout()
            }
        })
    }

    private fun showEmptyLayout() {
        binding.emptyLayout.noDataLayout.visibility = View.VISIBLE
        binding.weatherRecyclerView.visibility = View.GONE
    }

    private fun showLoadingLayout() {
        binding.weatherRecyclerView.visibility = View.GONE
        binding.emptyLayout.noDataLayout.visibility = View.GONE
        binding.loadingLayout.shimmerFrameLayout.startShimmer()
        binding.loadingLayout.shimmerFrameLayout.visibility = View.VISIBLE
    }

    private fun hideLoadingLayout() {
        binding.loadingLayout.shimmerFrameLayout.stopShimmer()
        binding.loadingLayout.shimmerFrameLayout.visibility = View.GONE
    }

    private fun setDataViewsVisibility(dataAvailable: Boolean) {
        if (dataAvailable) {
            binding.weatherRecyclerView.visibility = View.VISIBLE
            binding.emptyLayout.noDataLayout.visibility = View.GONE
        } else {
            binding.weatherRecyclerView.visibility = View.GONE
            binding.emptyLayout.noDataLayout.visibility = View.VISIBLE
        }
    }

    private fun handleWeatherPhotoListError(message: String) {
        setDataViewsVisibility(false)
        binding.emptyLayout.noDataTxtView.text = message
    }

    private fun setupRecycler(weatherPhotosList: List<WeatherPhotoDto>) {
        setDataViewsVisibility(true)
        val adapter = WeatherPhotoAdapter(weatherPhotosList, this)
        binding.weatherRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.weatherRecyclerView.adapter = adapter
        hideLoadingLayout()
    }

    override fun onWeatherPhotoClick(photoPath: String) {
        val bundle = Bundle()
        bundle.putString(WEATHER_PHOTO_EXTRA, photoPath)
        findNavController().navigate(R.id.action_homeFragment_to_shareFragment, bundle)
    }

    private fun onNewPhotoClicked() {
        val permissions =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        if (PermissionManager.isAllPermissionGranted(this, permissions)) {
            navigateToCamera()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    private fun navigateToCamera() {
        val cameraManager =
            requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val cameraList = enumerateCameras(cameraManager)
        cameraList.forEach {
            Log.d("logd", "${it.cameraId} ${it.format} ${it.title}")
        }
        val item = cameraList.find { it.title.contains("Back") } ?: cameraList.first()
        findNavController().navigate(
            R.id.action_homeFragment_to_cameraFragment,
            bundleOf(CAMERA_ID to item.cameraId, PIXEL_FORMAT to item.format)
        )
    }


}