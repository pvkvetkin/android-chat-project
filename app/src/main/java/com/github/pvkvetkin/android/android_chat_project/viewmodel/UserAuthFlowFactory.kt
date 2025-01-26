package com.github.pvkvetkin.android.android_chat_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.pvkvetkin.android.android_chat_project.network.SessionManager

class UserAuthFlowFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserAuthFlowViewModel::class.java)) {
            return UserAuthFlowViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unable to instantiate ViewModel for $modelClass")
    }
}
