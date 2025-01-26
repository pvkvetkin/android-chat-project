package com.github.pvkvetkin.android.android_chat_project.network

import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @FormUrlEncoded
    @POST("/addusr")
    suspend fun registerUser(@Field("name") username: String): Response<String>

    @POST("/login")
    suspend fun loginUser(@Body requestBody: Map<String, String>): Response<String>

    @POST("/logout")
    suspend fun logoutUser(@Header("X-Auth-Token") token: String): Response<Unit>
}
