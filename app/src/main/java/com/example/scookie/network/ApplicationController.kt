package com.example.scookie.network

import android.app.Application
import com.example.scookie.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by TakHyeongMin on 2019-07-07.
 */
class ApplicationController : Application() {
    lateinit var networkService: NetworkService

    //private val baseUrl = "https://jungnami.ml/"
    private val baseUrl = BuildConfig.BASE_URL

    companion object {
        lateinit var instance : ApplicationController
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        buildNetwork()
    }

    fun buildNetwork(){

        val builder = Retrofit.Builder()
        val retrofit = builder
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        networkService = retrofit.create(NetworkService::class.java)
    }
}