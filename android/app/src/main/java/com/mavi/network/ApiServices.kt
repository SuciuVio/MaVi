package com.mavi.network

import retrofit2.Response
import retrofit2.http.*

// Auth Service
interface AuthService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserData>

    @GET("auth/users")
    suspend fun getAllUsers(): Response<List<UserData>>

    @GET("auth/users/{user_id}")
    suspend fun getUserById(@Path("user_id") userId: Int): Response<UserData>
}

// Chat Service
interface ChatService {
    @POST("chat/messages/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<ApiResponse<MessageResponse>>

    @GET("chat/messages/{other_user_id}")
    suspend fun getMessages(@Path("other_user_id") otherUserId: Int): Response<List<MessageResponse>>

    @GET("chat/messages/unread")
    suspend fun getUnreadMessages(): Response<List<MessageResponse>>

    @PUT("chat/messages/{message_id}/read")
    suspend fun markAsRead(@Path("message_id") messageId: Int): Response<ApiResponse<MessageResponse>>

    @DELETE("chat/messages/{message_id}")
    suspend fun deleteMessage(@Path("message_id") messageId: Int): Response<ApiResponse<Any>>

    @GET("chat/conversations")
    suspend fun getConversations(): Response<List<UserData>>
}

// File Service
interface FileService {
    @POST("files/transfer/init")
    suspend fun initFileTransfer(@Body request: InitFileTransferRequest): Response<ApiResponse<FileTransferResponse>>

    @GET("files/transfer/{transfer_id}")
    suspend fun getTransferStatus(@Path("transfer_id") transferId: Int): Response<FileTransferResponse>

    @GET("files/incoming")
    suspend fun getIncomingTransfers(): Response<List<FileTransferResponse>>

    @GET("files/outgoing")
    suspend fun getOutgoingTransfers(): Response<List<FileTransferResponse>>

    @PUT("files/transfer/{transfer_id}/cancel")
    suspend fun cancelTransfer(@Path("transfer_id") transferId: Int): Response<ApiResponse<FileTransferResponse>>
}