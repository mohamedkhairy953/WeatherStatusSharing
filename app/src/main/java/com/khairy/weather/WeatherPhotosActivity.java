package com.khairy.weather;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.khairy.core.listeners.OnFragmentInteractionListener;
import com.khairy.weather.databinding.ActivityWeatherBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WeatherPhotosActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    ActivityWeatherBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather);
        setSupportActionBar(binding.toolbar);
        setupNavigationComponent();
    }

    private void setupNavigationComponent() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Navigation.findNavController(this,R.id.nav_host_fragment).navigateUp();
        return true;
    }


    @Override
    public void setToolbarTitle(String title) {
        binding.toolbar.setTitle(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
            fragment.onActivityResult(requestCode,resultCode,data);
        }
    }
}
