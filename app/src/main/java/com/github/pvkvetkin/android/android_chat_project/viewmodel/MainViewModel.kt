package com.github.pvkvetkin.android.android_chat_project.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pvkvetkin.android.android_chat_project.model.Chat
import com.github.pvkvetkin.android.android_chat_project.model.Message
import com.github.pvkvetkin.android.android_chat_project.model.request.MessageData
import com.github.pvkvetkin.android.android_chat_project.network.RetrofitService
import com.github.pvkvetkin.android.android_chat_project.network.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionBridge: SessionManager,
    private val conversationGateway: ChatService
) : ViewModel() {

    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Loading)
    val state = _state.asStateFlow()

    private val _selectedChat = MutableStateFlow<Chat?>(null)
    val selectedChatOrChannel = _selectedChat.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun loadChats() {
        viewModelScope.launch {
            try {
                val token = sessionBridge.token.firstOrNull().orEmpty()
                val alias = sessionBridge.username.firstOrNull().orEmpty()

                if (token.isEmpty() || alias.isEmpty()) {
                    _state.value = MainScreenState.Error("Token or username is missing.")
                    return@launch
                }

                val chatsAndChannels = conversationGateway.getChats(alias, token) {
                    logout()
                }
                _state.value = MainScreenState.Success(chatsAndChannels)
            } catch (e: Exception) {
                _state.value = MainScreenState.Error(e.message ?: "Unknown error.")
            }
        }
    }

    private fun loadMessagesForChat(chat: Chat) {
        viewModelScope.launch {
            try {
                val token = sessionBridge.token.firstOrNull().orEmpty()
                val alias = sessionBridge.username.firstOrNull().orEmpty()

                if (token.isEmpty() || alias.isEmpty()) {
                    _state.value = MainScreenState.Error("Token or username is missing.")
                    return@launch
                }

                val threadMessages = conversationGateway.getMessagesForChat(
                    username = alias,
                    channel = chat,
                    token = token
                ) {
                    logout()
                }

                _messages.value = threadMessages
                chat.lastKnownId = threadMessages.minOfOrNull { it.id } ?: chat.lastKnownId
            } catch (e: Exception) {
                Log.e("MainViewModel", e.message ?: "Failed to load messages.")
                _messages.value = emptyList()
            }
        }
    }

    fun selectChat(item: Chat?) {
        _selectedChat.value = item
        if (item != null) {
            loadMessagesForChat(item)
        } else {
            _messages.value = emptyList()
        }
    }

    fun sendMessage(chatId: String, text: String) {
        viewModelScope.launch {
            try {
                val token = sessionBridge.token.firstOrNull()
                val alias = sessionBridge.username.firstOrNull()

                if (token.isNullOrEmpty() || alias.isNullOrEmpty()) {
                    _state.value = MainScreenState.Error("Token or username is missing.")
                    return@launch
                }

                val result = conversationGateway.sendMessage(
                    from = alias,
                    to = chatId,
                    data = MessageData.Text(text),
                    token = token
                ) {
                    logout()
                }

                if (result.isSuccess) {
                    selectedChatOrChannel.value?.let { loadMessagesForChat(it) }
                } else {
                    Log.e(
                        "MainViewModel",
                        "Failed to send message: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error sending message.", e)
            }
        }
    }

    suspend fun loadMoreMessages(chat: Chat): Boolean {
        return try {
            val token = sessionBridge.token.firstOrNull().orEmpty()
            val alias = sessionBridge.username.firstOrNull().orEmpty()

            if (token.isEmpty()) {
                _state.value = MainScreenState.Error("Token is missing.")
                return false
            }
            Log.d("MainViewModel", "Loading more messages. Last known ID: ${chat.lastKnownId}")

            val newMessages = conversationGateway.getMessagesForChat(
                username = alias,
                channel = chat,
                token = token,
                lastKnownId = chat.lastKnownId,
                rev = true
            ) {
                logout()
            }

            if (newMessages.isNotEmpty()) {
                _messages.value += newMessages
                chat.lastKnownId = newMessages.minOfOrNull { it.id } ?: chat.lastKnownId
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", e.message ?: "Failed to load latest messages.")
            false
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                val token = sessionBridge.token.firstOrNull()
                if (!token.isNullOrEmpty()) {
                    val response = RetrofitService.authApiService.logoutUser(token)
                    if (response.isSuccessful) {
                        Log.d("MainViewModel", "Logout confirmed by server.")
                    } else {
                        Log.d("MainViewModel", "Server logout failed: ${response.message()}")
                    }
                }
                sessionBridge.clearSession()
                _state.value = MainScreenState.Loading
            } catch (e: Exception) {
                Log.d("MainViewModel", "Logout error: ${e.message}")
            }
        }
    }

    sealed class MainScreenState {
        object Loading : MainScreenState()
        data class Success(val chatsAndChannels: List<Chat>) : MainScreenState()
        data class Error(val message: String) : MainScreenState()
    }
}
