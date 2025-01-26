package com.github.pvkvetkin.android.android_chat_project.viewmodel

import com.github.pvkvetkin.android.android_chat_project.model.Chat
import com.github.pvkvetkin.android.android_chat_project.model.Message
import com.github.pvkvetkin.android.android_chat_project.model.request.MessageData
import com.github.pvkvetkin.android.android_chat_project.model.request.MessageRequest
import com.github.pvkvetkin.android.android_chat_project.network.MessagesApiService
import com.github.pvkvetkin.android.android_chat_project.room.dao.ChatDao
import com.github.pvkvetkin.android.android_chat_project.room.dao.MessageDao
import com.github.pvkvetkin.android.android_chat_project.room.entity.ChatEntity

class ChatService(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val messagesApiService: MessagesApiService
) {

    suspend fun getMessagesForChat(
        username: String,
        channel: Chat,
        token: String,
        lastKnownId: Int = Int.MAX_VALUE,
        limit: Int = 20,
        rev: Boolean = true,
        onUnauthorized: suspend () -> Unit
    ): List<Message> {
        val allMessages = mutableListOf<Message>()
        var currentLastKnownId = lastKnownId

        try {
            while (true) {
                val messagesResponse = if (channel.isChannel) {
                    messagesApiService.getChannelMessages(
                        name = channel.name,
                        token = token,
                        limit = limit,
                        lastKnownId = currentLastKnownId,
                        reverse = rev
                    )
                } else {
                    messagesApiService.getInbox(
                        username = username,
                        token = token,
                        limit = limit,
                        lastKnownId = currentLastKnownId,
                        reverse = rev
                    )
                }

                if (!messagesResponse.isSuccessful) {
                    if (messagesResponse.code() == 401) {
                        onUnauthorized()
                        throw Exception("Unauthorized: Token expired or invalid")
                    }
                    throw Exception("Failed to fetch messages from network: ${messagesResponse.message()}")
                }

                val fetchedMessages = messagesResponse.body().orEmpty()
                if (fetchedMessages.isEmpty()) break

                val filteredMessages = fetchedMessages.filter {
                    it.from == channel.name || it.to == channel.name
                }

                allMessages.addAll(filteredMessages)
                currentLastKnownId = fetchedMessages.minOfOrNull { it.id } ?: 0

                updateLocalDatabase(filteredMessages)

                if (allMessages.size >= limit) break
            }
        } catch (e: Exception) {
            return messageDao.getMessagesByNameWithLimit(
                channel.name,
                lastKnownId = currentLastKnownId,
                limit = limit
            ).map { entity ->
                val messageData = when {
                    !entity.text.isNullOrEmpty() -> MessageData.Text(entity.text)
                    !entity.imageLink.isNullOrEmpty() -> MessageData.Image(entity.imageLink)
                    else -> error("MessageEntity must have either text or imageLink.")
                }
                Message(
                    id = entity.id,
                    from = entity.from,
                    to = entity.to,
                    data = messageData,
                    time = entity.time
                )
            }
        }
        return allMessages
    }

    private suspend fun updateLocalDatabase(messages: List<Message>) {
        messageDao.insertMessages(messages.map { it.toEntity() })
    }

    suspend fun getChats(
        username: String,
        token: String,
        onUnauthorized: suspend () -> Unit
    ): List<Chat> {
        return try {
            val channelsResponse = messagesApiService.getChannels(token)
            if (!channelsResponse.isSuccessful) {
                if (channelsResponse.code() == 401) {
                    onUnauthorized()
                    throw Exception("Unauthorized: Token expired or invalid")
                }
                throw Exception("Failed to fetch channels from network: ${channelsResponse.message()}")
            }

            val channels = channelsResponse.body().orEmpty()
            val uniqueChats = mutableSetOf<String>()
            val chats = mutableListOf<Chat>()

            var lastKnownId = Int.MAX_VALUE
            val pageSize = 100

            while (true) {
                val messagesResponse = messagesApiService.getInbox(
                    username = username,
                    token = token,
                    lastKnownId = lastKnownId,
                    limit = pageSize,
                    reverse = true
                )

                if (!messagesResponse.isSuccessful) {
                    if (messagesResponse.code() == 401) {
                        onUnauthorized()
                        throw Exception("Unauthorized: Token expired or invalid")
                    }
                    throw Exception("Failed to fetch inbox messages from network: ${messagesResponse.message()}")
                }

                val fetchedMessages = messagesResponse.body().orEmpty()
                if (fetchedMessages.isEmpty()) break

                val filteredMessages = fetchedMessages.filter { it.to != null && !it.to!!.contains("@channel") }
                filteredMessages.forEach { message ->
                    val otherParty = if (message.from == username) message.to!! else message.from
                    if (otherParty !in uniqueChats) {
                        uniqueChats.add(otherParty)
                        chats.add(Chat(otherParty, isChannel = false))
                    }
                }
                lastKnownId = fetchedMessages.minOfOrNull { it.id } ?: 0
            }

            val allItems = chats + channels.map {
                Chat(name = it, isChannel = true)
            }
            updateLocalChats(allItems, username)
            allItems
        } catch (e: Exception) {
            if (e.message?.contains("Unauthorized") == true) {
                throw e
            }
            val localChats = chatDao.getUserChats(username)
            localChats.map { Chat(it.name, isChannel = it.isChannel) }
        }
    }

    private suspend fun updateLocalChats(channels: List<Chat>, username: String) {
        val chatEntities = channels.map {
            ChatEntity(
                name = it.name,
                owner = username,
                isChannel = it.isChannel
            )
        }
        chatDao.insertChats(chatEntities)
    }

    suspend fun sendMessage(
        from: String,
        to: String,
        data: MessageData,
        token: String,
        onUnauthorized: suspend () -> Unit
    ): Result<Unit> {
        return try {
            val messageRequest = MessageRequest(from = from, to = to, data = data)
            val response = messagesApiService.sendMessage(token, messageRequest)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                if (response.code() == 401) {
                    onUnauthorized()
                    throw Exception("Unauthorized: Token expired or invalid")
                }
                Result.failure(Exception("Failed to send message: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
