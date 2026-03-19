package com.unifor.comprovaentrega.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unifor.comprovaentrega.data.local.dao.DeliveryDao
import com.unifor.comprovaentrega.data.local.entity.Delivery

@Database(entities = [Delivery::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deliveryDao(): DeliveryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "comprova_entrega_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}