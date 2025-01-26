package com.github.pvkvetkin.android.android_chat_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.github.pvkvetkin.android.android_chat_project.network.RetrofitService
import com.github.pvkvetkin.android.android_chat_project.network.SessionManager
import com.github.pvkvetkin.android.android_chat_project.room.AppDatabase
import com.github.pvkvetkin.android.android_chat_project.ui.screens.RootScreen
import com.github.pvkvetkin.android.android_chat_project.ui.theme.AndroidchatprojectTheme
import com.github.pvkvetkin.android.android_chat_project.viewmodel.ChatService
import com.github.pvkvetkin.android.android_chat_project.viewmodel.MainViewModel
import com.github.pvkvetkin.android.android_chat_project.viewmodel.MainViewModelFactory
import com.github.pvkvetkin.android.android_chat_project.viewmodel.UserAuthFlowFactory
import com.github.pvkvetkin.android.android_chat_project.viewmodel.UserAuthFlowViewModel

class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        val localDb = AppDatabase.getInstance(this)
        val chatAccess = localDb.chatDao()
        val messageAccess = localDb.messageDao()
        val messagesApiService = RetrofitService.messagesApiService
        val chatService = ChatService(chatAccess, messageAccess, messagesApiService)
        val authFlowCoordinator = ViewModelProvider(
            this,
            UserAuthFlowFactory(sessionManager)
        )[UserAuthFlowViewModel::class.java]

        val mainCoordinator = ViewModelProvider(

            this,
            MainViewModelFactory(sessionManager, chatService)
        )[MainViewModel::class.java]

        setContent {
            AndroidchatprojectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RootScreen(
                        userAuthFlowViewModel = authFlowCoordinator,
                        mainViewModel = mainCoordinator
                    )
                }
            }
        }
    }
}
