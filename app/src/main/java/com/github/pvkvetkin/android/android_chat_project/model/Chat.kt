package com.github.pvkvetkin.android.android_chat_project.model

data class Chat(
    val name: String,
    val isChannel: Boolean,
    var lastKnownId: Int = Int.MAX_VALUE
)
