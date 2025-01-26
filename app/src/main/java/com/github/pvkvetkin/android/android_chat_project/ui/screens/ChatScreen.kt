package com.github.pvkvetkin.android.android_chat_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.github.pvkvetkin.android.android_chat_project.R
import com.github.pvkvetkin.android.android_chat_project.model.Chat
import com.github.pvkvetkin.android.android_chat_project.model.Message
import com.github.pvkvetkin.android.android_chat_project.model.request.MessageData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chat: Chat,
    onBack: () -> Unit,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onLoadMoreMessages: suspend () -> Boolean
) {
    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var canLoadMore by remember { mutableStateOf(true) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty() && listState.firstVisibleItemIndex == 0) {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index, canLoadMore) {
        val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        if (lastVisibleIndex == messages.lastIndex && canLoadMore) {
            canLoadMore = onLoadMoreMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ConversationHeaderView(chatName = chat.name, onBack = onBack)
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
            reverseLayout = true,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messages) { message ->
                MessageRow(message)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Write your message...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFE0E0E0),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Button(
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        onSendMessage(inputMessage)
                        inputMessage = ""
                    }
                },
                enabled = inputMessage.isNotBlank()
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun ConversationHeaderView(chatName: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF303030))
            .statusBarsPadding()
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = "Chat $chatName",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun MessageRow(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.Top)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = message.from,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                when (val data = message.data) {
                    is MessageData.Text -> {
                        Text(
                            text = data.text,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    is MessageData.Image -> {
                        data.link?.let {
                            ImageMessageRow(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageMessageRow(imagePath: String) {
    var showFullScreenImage by remember { mutableStateOf(false) }
    AsyncImage(
        model = stringResource(R.string.img_url_api, "thumb/$imagePath"),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RectangleShape)
            .clickable { showFullScreenImage = true }
    )
    if (showFullScreenImage) {
        FullScreenImageModal(
            imageUrl = stringResource(R.string.img_url_api, "img/$imagePath"),
            onClose = { showFullScreenImage = false }
        )
    }
}

@Composable
fun FullScreenImageModal(imageUrl: String, onClose: () -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(24.dp)
                    .clickable { onClose() }
            )
        }
    }
}
