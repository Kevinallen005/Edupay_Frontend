package com.simats.feepayment.retrofit
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object retrofit {
    const val BASE_URL = "http:/192.168.137.193/feespayment/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)   // Connect timeout
        .readTimeout(60, TimeUnit.SECONDS)      // Read timeout
        .writeTimeout(60, TimeUnit.SECONDS)     // Write timeout
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


}