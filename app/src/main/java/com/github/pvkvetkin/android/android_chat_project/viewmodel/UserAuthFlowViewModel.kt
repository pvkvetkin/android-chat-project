package com.github.pvkvetkin.android.android_chat_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pvkvetkin.android.android_chat_project.network.RetrofitService
import com.github.pvkvetkin.android.android_chat_project.network.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class UserAuthFlowViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _flowState = MutableStateFlow<AuthState>(AuthState.Dormant)
    val flowState = _flowState.asStateFlow()

    init {
        viewModelScope.launch {
            val savedToken = sessionManager.token.firstOrNull()
            val savedUser = sessionManager.username.firstOrNull()
            if (!savedToken.isNullOrEmpty() && !savedUser.isNullOrEmpty()) {
                _flowState.value = AuthState.Active(savedToken)
            } else {
                _flowState.value = AuthState.Dormant
            }

            sessionManager.token.combine(sessionManager.username) { token, user ->
                token to user
            }.collect { (token, user) ->
                if (token.isNullOrEmpty() || user.isNullOrEmpty()) {
                    _flowState.value = AuthState.Dormant
                }
            }
        }
    }

    fun registerAccount(nickname: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitService.authApiService.registerUser(nickname)
                if (response.isSuccessful) {
                    val rawBody = response.body().orEmpty()
                    val parsedSecret = rawBody.substringAfter("password: '").substringBefore("'")
                        .ifEmpty { "No password found" }
                    _flowState.value = AuthState.Created(parsedSecret)
                } else {
                    _flowState.value = AuthState.Failure(
                        "Account creation failed: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _flowState.value = AuthState.Failure(e.message ?: "Unknown issue")
            }
        }
    }

    fun authorizeUser(alias: String, secretKey: String) {
        viewModelScope.launch {
            try {
                val requestData = mapOf("name" to alias, "pwd" to secretKey)
                val response = RetrofitService.authApiService.loginUser(requestData)
                if (response.isSuccessful) {
                    val token = response.body().orEmpty()
                    sessionManager.saveSession(token, alias)
                    _flowState.value = AuthState.Active(token)
                } else {
                    _flowState.value = AuthState.Failure(
                        "Authorization failed: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _flowState.value = AuthState.Failure(e.message ?: "Unknown issue")
            }
        }
    }

    sealed class AuthState {
        object Dormant : AuthState()
        data class Created(val secret: String) : AuthState()
        data class Active(val token: String) : AuthState()
        data class Failure(val reason: String) : AuthState()
    }
}
