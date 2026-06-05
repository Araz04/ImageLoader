package com.task.imageloader.sample.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.task.imageloader.core.ImageLoader
import com.task.imageloader.sample.repository.ApiService
import com.task.imageloader.sample.repository.ImageRepository
import com.task.imageloader.sample.stateholder.ImageViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://zipoapps-storage-test.nyc3.digitaloceanspaces.com/"

val appModule = module {

    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    single<ApiService> {
        get<Retrofit>().create(ApiService::class.java)
    }

    single { ImageRepository(get()) }

    single { ImageLoader.getInstance(androidContext()) }

    viewModel { ImageViewModel(androidApplication(), get(), get()) }
}