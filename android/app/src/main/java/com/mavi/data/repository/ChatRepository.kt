package com.mavi.data.repository

import com.mavi.data.local.AppDatabase
import com.mavi.data.local.MessageEntity
import com.mavi.network.ApiClient
import com.mavi.network.SendMessageRequest

class ChatRepository(private val db: AppDatabase) {
    private val chatService = ApiClient.chatService

    fun getConversation(userId: Int, otherUserId: Int) = 
        db.messageDao().getConversation(userId, otherUserId)

    fun getUnreadMessages(userId: Int) = 
        db.messageDao().getUnreadMessages(userId)

    suspend fun sendMessage(receiverId: Int, content: String): Result<Unit> = try {
        val response = chatService.sendMessage(SendMessageRequest(receiverId, content))
        if (response.isSuccessful) {
            response.body()?.data?.let { msg ->
                db.messageDao().insertMessage(
                    MessageEntity(
                        msg.id, msg.sender_id, msg.sender_username,
                        msg.receiver_id, msg.receiver_username, msg.content,
                        msg.timestamp, msg.is_read
                    )
                )
            }
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun fetchMessages(otherUserId: Int): Result<Unit> = try {
        val response = chatService.getMessages(otherUserId)
        if (response.isSuccessful) {
            response.body()?.let { messages ->
                db.messageDao().insertMessages(messages.map { msg ->
                    MessageEntity(
                        msg.id, msg.sender_id, msg.sender_username,
                        msg.receiver_id, msg.receiver_username, msg.content,
                        msg.timestamp, msg.is_read
                    )
                })
            }
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun markAsRead(messageId: Int): Result<Unit> = try {
        val response = chatService.markAsRead(messageId)
        if (response.isSuccessful) {
            db.messageDao().markAsRead(messageId)
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteMessage(messageId: Int): Result<Unit> = try {
        val response = chatService.deleteMessage(messageId)
        if (response.isSuccessful) {
            db.messageDao().deleteMessage(messageId)
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
