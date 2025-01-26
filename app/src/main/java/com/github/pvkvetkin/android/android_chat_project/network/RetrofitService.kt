package com.github.pvkvetkin.android.android_chat_project.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RetrofitService {
    private const val BASE_URL = "https://faerytea.name:8008/"

    private val moshi = Moshi.Builder()
        .add(CasualJsonDataAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .client(OkHttpClient())
        .build()

    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val messagesApiService: MessagesApiService = retrofit.create(MessagesApiService::class.java)
}
