package com.mavi.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("""
        SELECT * FROM messages 
        WHERE (sender_id = :userId AND receiver_id = :otherUserId) 
           OR (sender_id = :otherUserId AND receiver_id = :userId)
        ORDER BY timestamp ASC
    """)
    fun getConversation(userId: Int, otherUserId: Int): Flow<List<MessageEntity>>

    @Query("""
        SELECT * FROM messages 
        WHERE receiver_id = :userId AND is_read = 0
        ORDER BY timestamp DESC
    """)
    fun getUnreadMessages(userId: Int): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET is_read = 1 WHERE id = :messageId")
    suspend fun markAsRead(messageId: Int)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Int)

    @Delete
    suspend fun delete(message: MessageEntity)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()
}
