package com.example.android.bluetoothlegatt

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = arrayOf(BleListEntity::class), version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun BleDao(): BleDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context,
                        scope: CoroutineScope
        ): AppDatabase? {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
        private class WordDatabaseCallback(
                private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.BleDao())
                    }
                }
            }
        }
        fun populateDatabase(bleDao: BleDao) {
            bleDao.clearAll()

            var ble = BleListEntity(1,"name1","addr1")
            bleDao.insert(ble)
            ble = BleListEntity(2,"name2","addr2")
            bleDao.insert(ble)
        }
    }

}