package com.maryang.fastrxjava.data.source

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object ApiManager {

    private val gson =
        GsonBuilder()
            .setLenient()
            .create()

    private val okHttpClientBuilder =
        OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

    private val githubAdapter by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClientBuilder
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                        .header("Accept", "application/vnd.github.v3+json")
                    chain.proceed(requestBuilder.build())
                }
                .build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // rxjava 쓰려면 추가해야함.
            .build()
    }

    val githubApi: GithubApi by lazy {
        githubAdapter.create(GithubApi::class.java)
    }
}
