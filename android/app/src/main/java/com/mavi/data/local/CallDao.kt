package com.mavi.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(call: CallEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalls(calls: List<CallEntity>)

    @Query("SELECT * FROM calls WHERE call_id = :callId")
    suspend fun getCallById(callId: String): CallEntity?

    @Query("""
        SELECT * FROM calls 
        WHERE receiver_id = :userId AND status = 'ringing'
        ORDER BY started_at DESC
    """)
    fun getIncomingCalls(userId: Int): Flow<List<CallEntity>>

    @Query("""
        SELECT * FROM calls 
        WHERE (caller_id = :userId OR receiver_id = :userId) 
        AND status IN ('ringing', 'accepted')
        ORDER BY started_at DESC
    """)
    fun getActiveCalls(userId: Int): Flow<List<CallEntity>>

    @Query("""
        SELECT * FROM calls 
        WHERE (caller_id = :userId OR receiver_id = :userId)
        ORDER BY started_at DESC
    """)
    fun getAllCalls(userId: Int): Flow<List<CallEntity>>

    @Query("UPDATE calls SET status = :status WHERE call_id = :callId")
    suspend fun updateStatus(callId: String, status: String)

    @Query("UPDATE calls SET ended_at = :endedAt, status = 'ended' WHERE call_id = :callId")
    suspend fun endCall(callId: String, endedAt: String)

    @Delete
    suspend fun delete(call: CallEntity)

    @Query("DELETE FROM calls")
    suspend fun deleteAll()
}
