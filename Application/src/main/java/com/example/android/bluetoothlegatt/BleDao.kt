package com.example.android.bluetoothlegatt

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BleDao {

    @Query("SELECT * FROM BleListEntity")
    fun getAllDevice(): LiveData<List<BleListEntity>>

    @Query("DELETE FROM BleListEntity")
    fun clearAll()

    //해당 데이터를 추가합니다.

   /* @Insert
    fun insert(vararg person: com.example.android.bluetoothlegatt.BleListEntity)*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg person: BleListEntity)

    //헤당 데이터를 업데이트 합니다.
    @Update
    fun update(vararg person: BleListEntity)

    //해당 데이터를 삭제합니다.
    @Delete
    fun delete(vararg person: BleListEntity)

}