package com.github.pvkvetkin.android.android_chat_project.model

import com.github.pvkvetkin.android.android_chat_project.model.request.MessageData
import com.github.pvkvetkin.android.android_chat_project.room.entity.MessageEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    val id: Int,
    val from: String,
    val to: String,
    val data: MessageData,
    val time: String?
) {
    fun toEntity(): MessageEntity {
        return MessageEntity(
            id = this.id,
            from = this.from,
            to = this.to,
            text = (this.data as? MessageData.Text)?.text, 
            imageLink = (this.data as? MessageData.Image)?.link, 
            time = this.time.orEmpty() 
        )
    }

}
