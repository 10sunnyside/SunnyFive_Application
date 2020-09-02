package com.example.android.bluetoothlegatt

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class BleRepository(private val bleDao: BleDao?) {

    val allBleListEntity: LiveData<List<BleListEntity>> = bleDao?.getAllDevice()!!

    @WorkerThread
    suspend fun insert(ble: BleListEntity) {
        bleDao?.insert(ble)
    }
}