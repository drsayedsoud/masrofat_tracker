package com.eltafouk.masareefi.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class MasareefiDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: MasareefiDatabase? = null

        fun getInstance(context: Context): MasareefiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MasareefiDatabase::class.java,
                    "masareefi_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
