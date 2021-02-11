package com.khairy.core.di

import android.content.Context
import com.khairy.core.BuildConfig
import com.khairy.core.data.db.WeatherDatabase.Companion.getDatabaseInstance
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton
import com.khairy.core.data.network.NetworkUtils
import okhttp3.logging.HttpLoggingInterceptor
import com.khairy.core.data.network.WeatherInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import com.khairy.core.data.db.WeatherDatabase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

@InstallIn(ApplicationComponent::class)
@Module
class ServiceModule {

    @Provides
    @Singleton
    fun provideRestService(
        client: OkHttpClient
    ):Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create()) //the ordering is importing, we must but ScalersConverter before Gson
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        myServiceInterceptor: WeatherInterceptor,
        interceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(NetworkUtils.CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(NetworkUtils.READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(NetworkUtils.WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.interceptors().add(interceptor)
        builder.interceptors().add(myServiceInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
     fun provideServiceInterceptor() = WeatherInterceptor()

    @Provides
    @Singleton
     fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG)
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return getDatabaseInstance(context)
    }
}