package com.example.dchat

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8000/"//change when submitting
    private val client = OkHttpClient
        .Builder()
        .addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            if (SessionManager.jwtToken.isNotEmpty()) {
                builder.addHeader(
                    "Authorization",
                    "Bearer ${SessionManager.jwtToken}"
                )
            }
            chain.proceed(builder.build())
        }
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}