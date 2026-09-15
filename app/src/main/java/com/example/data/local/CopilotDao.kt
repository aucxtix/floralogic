package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CopilotDao {
    @Query("SELECT * FROM copilot_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<CopilotMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: CopilotMessageEntity)

    @Query("DELETE FROM copilot_messages")
    suspend fun clearHistory()
}
