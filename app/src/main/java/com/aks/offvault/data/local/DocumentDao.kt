package com.aks.offvault.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aks.offvault.data.model.Document
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<Document>>

    @Query("SELECT * FROM documents WHERE id = :id")
    fun getDocumentById(id: Long): Flow<Document?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: Document): Long

    @Update
    suspend fun updateDocument(document: Document)

    @Delete
    suspend fun deleteDocument(document: Document)
}