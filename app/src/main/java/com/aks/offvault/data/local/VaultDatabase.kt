package com.aks.offvault.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aks.offvault.data.model.Card
import com.aks.offvault.data.model.Document
import com.aks.offvault.data.model.LoginDetail
import com.aks.offvault.data.model.Other
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [Card::class, Document::class, LoginDetail::class, Other::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VaultDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun documentDao(): DocumentDao
    abstract fun loginDetailDao(): LoginDetailDao
    abstract fun otherDao(): OtherDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        private const val META_PREFS = "vault_meta"
        private const val KEY_ENCRYPTION_READY = "encryption_initialized"

        fun getInstance(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                migrateToEncryptedIfNeeded(context)

                val passphrase = VaultKeyManager.getOrCreatePassphrase(context)
                val factory = SupportFactory(passphrase)
                passphrase.fill(0)

                Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    "vault.db"
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }

        // On the first launch after encryption is added, delete any pre-existing
        // plaintext database so SQLCipher can create a fresh encrypted one.
        private fun migrateToEncryptedIfNeeded(context: Context) {
            val prefs = context.getSharedPreferences(META_PREFS, Context.MODE_PRIVATE)
            if (prefs.getBoolean(KEY_ENCRYPTION_READY, false)) return

            context.getDatabasePath("vault.db").delete()
            context.getDatabasePath("vault.db-wal").delete()
            context.getDatabasePath("vault.db-shm").delete()

            prefs.edit().putBoolean(KEY_ENCRYPTION_READY, true).apply()
        }
    }
}
