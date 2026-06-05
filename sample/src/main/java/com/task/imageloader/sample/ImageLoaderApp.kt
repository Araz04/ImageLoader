package com.task.imageloader.sample

import android.app.Application
import com.task.imageloader.sample.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ImageLoaderApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ImageLoaderApp)
            modules(appModule)
        }
    }
}