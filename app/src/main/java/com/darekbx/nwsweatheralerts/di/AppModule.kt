package com.darekbx.nwsweatheralerts.di

import com.darekbx.nwsweatheralerts.BuildConfig
import com.darekbx.nwsweatheralerts.repository.remote.NWSService
import com.darekbx.nwsweatheralerts.ui.NWSViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val BASE_URL = "https://api.weather.gov"

val networkModule = module {
    single<NWSService> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NWSService::class.java)
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }.build()
    }
}

val viewModelModule = module {
    viewModel { NWSViewModel(get()) }
}

val appModule = module {

}