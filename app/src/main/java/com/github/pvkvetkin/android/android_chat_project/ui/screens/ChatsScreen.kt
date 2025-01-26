package com.github.pvkvetkin.android.android_chat_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.pvkvetkin.android.android_chat_project.model.Chat
import com.github.pvkvetkin.android.android_chat_project.viewmodel.MainViewModel

@Composable
fun ChatsScreen(
    state: MainViewModel.MainScreenState,
    onChatSelected: (Chat) -> Unit,
    onLogoutClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        HeaderView(title = "All chats", onLogoutClicked = onLogoutClicked)
        when (state) {
            is MainViewModel.MainScreenState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MainViewModel.MainScreenState.Success -> {
                val items = state.chatsAndChannels
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onChatSelected(item) },
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 8.dp)
                                )
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
            is MainViewModel.MainScreenState.Error -> {
                Text(
                    text = "Oh no! Something's off: ${state.message}",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun HeaderView(title: String, onLogoutClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF303030))
            .statusBarsPadding()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            TextButton(
                onClick = onLogoutClicked,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Logout")
            }
        }
    }
}
