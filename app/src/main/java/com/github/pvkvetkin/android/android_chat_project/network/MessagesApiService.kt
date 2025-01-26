package com.github.pvkvetkin.android.android_chat_project.network

import com.github.pvkvetkin.android.android_chat_project.model.Message
import com.github.pvkvetkin.android.android_chat_project.model.request.MessageRequest
import retrofit2.Response
import retrofit2.http.*

interface MessagesApiService {

    @GET("/channels")
    suspend fun getChannels(@Header("X-Auth-Token") token: String): Response<List<String>>

    @POST("/messages")
    suspend fun sendMessage(
        @Header("X-Auth-Token") token: String,
        @Body message: MessageRequest
    ): Response<Int>

    @GET("/channel/{name}")
    suspend fun getChannelMessages(
        @Path("name") name: String,
        @Header("X-Auth-Token") token: String,
        @Query("limit") limit: Int? = null,
        @Query("lastKnownId") lastKnownId: Int? = null,
        @Query("reverse") reverse: Boolean? = null
    ): Response<List<Message>>

    @GET("/inbox/{username}")
    suspend fun getInbox(
        @Path("username") username: String,
        @Header("X-Auth-Token") token: String,
        @Query("limit") limit: Int? = null,
        @Query("lastKnownId") lastKnownId: Int? = null,
        @Query("reverse") reverse: Boolean? = null
    ): Response<List<Message>>
}
