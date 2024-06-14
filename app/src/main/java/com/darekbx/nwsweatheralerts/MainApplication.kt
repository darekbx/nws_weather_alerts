package com.darekbx.nwsweatheralerts

import android.app.Application
import com.darekbx.nwsweatheralerts.di.appModule
import com.darekbx.nwsweatheralerts.di.networkModule
import com.darekbx.nwsweatheralerts.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(networkModule, viewModelModule, appModule)
        }
    }
}
