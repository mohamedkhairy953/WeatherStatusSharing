package com.khairy.weather;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class WeatherPhotoApplication extends Application {

    private static WeatherPhotoApplication instance;

    public static WeatherPhotoApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
