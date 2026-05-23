package com.mavi.network

import com.google.gson.annotations.SerializedName

// Auth Models
data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val message: String,
    val access_token: String? = null,
    val user: UserData? = null
)

data class UserData(
    val id: Int,
    val username: String,
    val created_at: String
)

// Chat Models
data class SendMessageRequest(
    val receiver_id: Int,
    val content: String
)

data class MessageResponse(
    val id: Int,
    val sender_id: Int,
    val sender_username: String,
    val receiver_id: Int,
    val receiver_username: String,
    val content: String,
    val timestamp: String,
    val is_read: Boolean
)

data class ChatApiResponse<T>(
    val message: String,
    val data: T? = null
)

// File Transfer Models
data class InitFileTransferRequest(
    val receiver_id: Int,
    val filename: String,
    val file_size: Long
)

data class FileTransferResponse(
    val id: Int,
    val sender_id: Int,
    val sender_username: String,
    val receiver_id: Int,
    val receiver_username: String,
    val filename: String,
    val file_size: Long,
    val timestamp: String,
    val status: String
)

// Generic Response
data class ApiResponse<T>(
    val message: String,
    val data: T? = null
)