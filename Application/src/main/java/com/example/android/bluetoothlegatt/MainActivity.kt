package com.example.android.bluetoothlegatt

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.sunnyside_main.*


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity","beforeonCreate")
        println("zzzzzzzzz")
        super.onCreate(savedInstanceState)
        Log.d("MainActivity","beforeonCreate")
        setContentView(R.layout.sunnyside_main)
        Log.d("MainActivity","onCreate")
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, 1)
        setting.setOnClickListener{
            val intent = Intent(this, DeviceScanActivity::class.java)
            startActivity(intent)
        }
    }
}