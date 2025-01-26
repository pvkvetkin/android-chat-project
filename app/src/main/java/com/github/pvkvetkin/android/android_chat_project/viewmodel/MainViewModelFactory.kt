package com.github.pvkvetkin.android.android_chat_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.pvkvetkin.android.android_chat_project.network.SessionManager

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val sessionKeeper: SessionManager,
    private val conversationDriver: ChatService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(sessionKeeper, conversationDriver) as T
        }
        throw IllegalArgumentException("Unable to create instance for $modelClass")
    }
}
