package com.example.android.bluetoothlegatt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity

data class BleListEntity(

        @PrimaryKey(autoGenerate = true) val id :Int,
        val name:String?,
        val addr:String?
)