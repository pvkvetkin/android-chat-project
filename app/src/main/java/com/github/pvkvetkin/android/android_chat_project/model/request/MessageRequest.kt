package com.github.pvkvetkin.android.android_chat_project.model.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageRequest(
    val from: String,
    val to: String,
    val data: MessageData
)


@JsonClass(generateAdapter = true)
sealed class MessageData {
    data class Text(val text: String) : MessageData()
    data class Image(val link: String?) : MessageData()
}
