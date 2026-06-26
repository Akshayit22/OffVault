package com.aks.offvault.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aks.offvault.data.model.Other
import kotlinx.coroutines.flow.Flow

@Dao
interface OtherDao {

    @Query("SELECT * FROM others ORDER BY updatedAt DESC")
    fun getAllOthers(): Flow<List<Other>>

    @Query("SELECT * FROM others WHERE id = :id")
    fun getOtherById(id: Long): Flow<Other?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOther(other: Other): Long

    @Update
    suspend fun updateOther(other: Other)

    @Delete
    suspend fun deleteOther(other: Other)

    @Query("DELETE FROM others")
    suspend fun deleteAll()
}