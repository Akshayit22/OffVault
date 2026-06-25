package com.aks.offvault.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aks.offvault.data.model.Card
import com.aks.offvault.data.model.Document

@Database(entities = [Card::class, Document::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class VaultDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        fun getInstance(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    "vault.db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}