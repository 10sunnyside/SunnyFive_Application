package com.example.android.bluetoothlegatt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BleRepository
    val allBle: LiveData<List<BleListEntity>>

    init {
        val bleDao = AppDatabase.getInstance(application, viewModelScope)?.BleDao()
        repository = BleRepository(bleDao)
        allBle = repository.allBleListEntity
    }

    fun insert(bleListEntity: BleListEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(bleListEntity)
    }
}