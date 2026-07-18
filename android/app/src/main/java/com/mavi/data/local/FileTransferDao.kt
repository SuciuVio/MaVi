package com.mavi.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FileTransferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: FileTransferEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfers(transfers: List<FileTransferEntity>)

    @Query("SELECT * FROM file_transfers WHERE id = :transferId")
    suspend fun getTransferById(transferId: Int): FileTransferEntity?

    @Query("""
        SELECT * FROM file_transfers 
        WHERE receiver_id = :userId
        ORDER BY timestamp DESC
    """)
    fun getIncomingTransfers(userId: Int): Flow<List<FileTransferEntity>>

    @Query("""
        SELECT * FROM file_transfers 
        WHERE sender_id = :userId
        ORDER BY timestamp DESC
    """)
    fun getOutgoingTransfers(userId: Int): Flow<List<FileTransferEntity>>

    @Query("""
        SELECT * FROM file_transfers 
        WHERE (sender_id = :userId OR receiver_id = :userId)
        ORDER BY timestamp DESC
    """)
    fun getAllTransfers(userId: Int): Flow<List<FileTransferEntity>>

    @Query("UPDATE file_transfers SET status = :status WHERE id = :transferId")
    suspend fun updateStatus(transferId: Int, status: String)

    @Delete
    suspend fun delete(transfer: FileTransferEntity)

    @Query("DELETE FROM file_transfers")
    suspend fun deleteAll()
}
