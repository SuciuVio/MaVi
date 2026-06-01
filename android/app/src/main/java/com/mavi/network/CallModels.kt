package com.mavi.network

import retrofit2.Response
import retrofit2.http.*

// Call Models
data class InitiateCallRequest(
    val receiver_id: Int
)

data class CallResponse(
    val call_id: String,
    val caller_id: Int,
    val caller_username: String,
    val receiver_id: Int,
    val receiver_username: String,
    val status: String,
    val started_at: String,
    val ended_at: String? = null
)

data class CallStatusUpdate(
    val call_id: String,
    val status: String // "ringing", "accepted", "rejected", "ended"
)

// Call Service
interface CallService {
    @POST("calls/initiate")
    suspend fun initiateCall(@Body request: InitiateCallRequest): Response<ApiResponse<CallResponse>>

    @GET("calls/{call_id}")
    suspend fun getCallStatus(@Path("call_id") callId: String): Response<CallResponse>

    @PUT("calls/{call_id}/accept")
    suspend fun acceptCall(@Path("call_id") callId: String): Response<ApiResponse<CallResponse>>

    @PUT("calls/{call_id}/reject")
    suspend fun rejectCall(@Path("call_id") callId: String): Response<ApiResponse<CallResponse>>

    @PUT("calls/{call_id}/end")
    suspend fun endCall(@Path("call_id") callId: String): Response<ApiResponse<CallResponse>>

    @GET("calls/incoming")
    suspend fun getIncomingCalls(): Response<List<CallResponse>>

    @GET("calls/active")
    suspend fun getActiveCalls(): Response<List<CallResponse>>
}
