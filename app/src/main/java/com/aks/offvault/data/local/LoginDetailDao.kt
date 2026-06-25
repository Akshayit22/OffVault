package com.aks.offvault.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aks.offvault.data.model.LoginDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface LoginDetailDao {

    @Query("SELECT * FROM login_details ORDER BY updatedAt DESC")
    fun getAllLoginDetails(): Flow<List<LoginDetail>>

    @Query("SELECT * FROM login_details WHERE id = :id")
    fun getLoginDetailById(id: Long): Flow<LoginDetail?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoginDetail(loginDetail: LoginDetail): Long

    @Update
    suspend fun updateLoginDetail(loginDetail: LoginDetail)

    @Delete
    suspend fun deleteLoginDetail(loginDetail: LoginDetail)
}