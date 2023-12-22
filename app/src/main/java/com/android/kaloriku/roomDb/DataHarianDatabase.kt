package com.android.kaloriku.roomDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataHarian::class], version = 6, exportSchema = false)
abstract class DataHarianDatabase : RoomDatabase() {
    abstract fun dataHarianDao(): DataHarianDao

    companion object {
        @Volatile
        private var INSTANCE: DataHarianDatabase? = null

        fun getDatabase(context: Context): DataHarianDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataHarianDatabase::class.java, "data_harian"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}