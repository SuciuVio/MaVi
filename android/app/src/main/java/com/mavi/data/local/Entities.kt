package com.mavi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

// User Entity
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val username: String,
    val created_at: String
)

// Message Entity
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["sender_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiver_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: Int,
    val sender_id: Int,
    val sender_username: String,
    val receiver_id: Int,
    val receiver_username: String,
    val content: String,
    val timestamp: String,
    val is_read: Boolean
)

// File Transfer Entity
@Entity(
    tableName = "file_transfers",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["sender_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiver_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FileTransferEntity(
    @PrimaryKey
    val id: Int,
    val sender_id: Int,
    val sender_username: String,
    val receiver_id: Int,
    val receiver_username: String,
    val filename: String,
    val file_size: Long,
    val timestamp: String,
    val status: String // pending, completed, failed
)

// Call Entity
@Entity(
    tableName = "calls",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["caller_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiver_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CallEntity(
    @PrimaryKey
    val call_id: String,
    val caller_id: Int,
    val caller_username: String,
    val receiver_id: Int,
    val receiver_username: String,
    val status: String, // ringing, accepted, rejected, ended
    val started_at: String,
    val ended_at: String? = null
)
