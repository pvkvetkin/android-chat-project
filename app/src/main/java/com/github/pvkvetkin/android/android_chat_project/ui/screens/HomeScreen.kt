package com.github.pvkvetkin.android.android_chat_project.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.github.pvkvetkin.android.android_chat_project.viewmodel.MainViewModel

@Composable
fun HomeScreen(mainViewModel: MainViewModel) {
    val state by mainViewModel.state.collectAsState()
    val selectedChatOrChannel by mainViewModel.selectedChatOrChannel.collectAsState()
    val messages by mainViewModel.messages.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.loadChats()
    }

    BackHandler {
        when {
            selectedChatOrChannel != null -> mainViewModel.selectChat(null)
            else -> mainViewModel.logout()
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                ChatsScreen(
                    state = state,
                    onChatSelected = { mainViewModel.selectChat(it) },
                    onLogoutClicked = { mainViewModel.logout() }
                )
            }
            Box(modifier = Modifier.weight(2f)) {
                if (selectedChatOrChannel != null) {
                    val chatOrChannel = selectedChatOrChannel!!
                    ChatScreen(
                        chat = chatOrChannel,
                        onBack = { mainViewModel.selectChat(null) },
                        messages = messages,
                        onSendMessage = { mainViewModel.sendMessage(chatOrChannel.name, it) },
                        onLoadMoreMessages = { mainViewModel.loadMoreMessages(chatOrChannel) }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.Text("No conversation selected yet!")
                    }
                }
            }
        }
    } else {
        if (selectedChatOrChannel == null) {
            ChatsScreen(
                state = state,
                onChatSelected = { mainViewModel.selectChat(it) },
                onLogoutClicked = { mainViewModel.logout() }
            )
        } else {
            val chatOrChannel = selectedChatOrChannel!!
            ChatScreen(
                chat = chatOrChannel,
                onBack = { mainViewModel.selectChat(null) },
                messages = messages,
                onSendMessage = { mainViewModel.sendMessage(chatOrChannel.name, it) },
                onLoadMoreMessages = { mainViewModel.loadMoreMessages(chatOrChannel) }
            )
        }
    }
}
