/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.colorpicker.*
import java.io.*

import java.util.ArrayList

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */

const val PERMISSION_REQUEST_LOCATION = 0

class DeviceScanActivity : ListActivity() {
    private var mLeDeviceListAdapter: LeDeviceListAdapter? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanning: Boolean = false
    private var mHandler: Handler? = null
    private var mActivityName :String? = null
    private var bleL :BleList? = null
    private var bleNameList:Array<String>? = null
    private var bleAddressList:Array<String>? = null

    private var mBluetoothLeScanner: BluetoothLeScanner? = null



    public var lastTimeBackPressed = System.currentTimeMillis()

    // Device scan callback.
    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            if(mLeDeviceListAdapter==null){
                mLeDeviceListAdapter = LeDeviceListAdapter()
                listAdapter = mLeDeviceListAdapter
                scanLeDevice(true)
            }
            mLeDeviceListAdapter!!.addDevice(device)
            mLeDeviceListAdapter!!.notifyDataSetChanged()
        }
    }
    // Device scan callback for api level 21
    private val scanCallback: ScanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            runOnUiThread {
                mLeDeviceListAdapter!!.addDevice(result.device)
                mLeDeviceListAdapter!!.notifyDataSetChanged()
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    //권한 체크
        if(ContextCompat.checkSelfPermission(this@DeviceScanActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"위치 허용",Toast.LENGTH_SHORT).show()

        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@DeviceScanActivity, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this@DeviceScanActivity, "ble 통신을 위해 위치 권한이 요구됩니다.",Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this@DeviceScanActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_LOCATION)
            }else {
                Toast.makeText(this@DeviceScanActivity,"위치 허가를 받을 수 없습니다.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this@DeviceScanActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_LOCATION)
            }

        }




        val intent = intent
        mActivityName = intent.getStringExtra(EXTRAS_ACTIVITY_NAME)

/*        if (DeviceControlActivity.mConnected ){

            return
        }*/

/*        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = BleListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)*/






        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        actionBar!!.setTitle(R.string.title_devices)
       // actionBar!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        mHandler = Handler()

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }


        val file = File(this.filesDir, "sunnyside1.txt")
        //sunnyside.txt 로 고치면 기록된 아이디로 자동 로그인 동작

        if (file.exists()){


           if(mActivityName=="DeviceControlActivity"){


           }
           else{

               val stringBuilder: StringBuilder = StringBuilder()

               try {
                   var fileInputStream: FileInputStream? = null
                   fileInputStream = openFileInput("sunnyside.txt")
                   var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
                   val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
                   var text: String? = null
                   while ({ text = bufferedReader.readLine(); text }() != null) {
                       stringBuilder.append(text)
                   }
//Displaying data on EditText
                   //fileData.setText(stringBuilder.toString()).toString()
                   Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_SHORT).show()



                   // name address 저장하는 부분
                   var delimiter = "/"

                   val parts = stringBuilder.toString().split(delimiter)

                   print(parts)
                   try {
                       val intent = Intent(this, DeviceControlActivity::class.java)
                       intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, parts[0])
                       intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, parts[1])
                       if (mScanning) {
                           mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                           mScanning = false
                       }
                       startActivity(intent)
                   }
                   catch(e:IndexOutOfBoundsException){}

               }
               catch(e:FileNotFoundException){

               }


           }


        }
        else{


        }

/*
        val file:String = "sunnyside.txt"
        val data:String = "sunnyside"
        val fileOutputStream:FileOutputStream
        try {
            fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
        }catch (e: Exception){
            e.printStackTrace()
        }*/










        val builder = AlertDialog.Builder(this)

        builder.setTitle("안녕하세요.  ⎛⎝⎛° ͜ʖ°⎞⎠⎞ ").setMessage("list에서 SunnySide를 선택해주세요")

        val alertDialog = builder.create()

        alertDialog.show()


        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).isVisible = false
            menu.findItem(R.id.menu_multi).isVisible = true
            menu.findItem(R.id.menu_scan).isVisible = true
            menu.findItem(R.id.menu_refresh).actionView = null
        } else {
            menu.findItem(R.id.menu_stop).isVisible = true
            menu.findItem(R.id.menu_multi).isVisible = true
            menu.findItem(R.id.menu_scan).isVisible = false
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_scan -> {
                val nullCheck=mLeDeviceListAdapter?.clear()
                scanLeDevice(true)
            }
            R.id.menu_stop -> scanLeDevice(false)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (DeviceControlActivity.mConnected ){

            return
        }
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter!!.isEnabled) {
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = LeDeviceListAdapter()
        listAdapter = mLeDeviceListAdapter
        scanLeDevice(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        scanLeDevice(false)
        mLeDeviceListAdapter?.clear()
    }

    override fun onBackPressed() {

        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            //finish()
            finishAffinity()
            return
        }

            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this,"버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show()
        

    }


    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {

        val device = mLeDeviceListAdapter!!.getDevice(position) ?: return
        val intent = Intent(this, DeviceControlActivity::class.java)
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.name)
       intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.address)
        //위의 두줄이 기존 코드 1개의 ble만 넣는 코드





        if(v.background == null) {
            v.setBackgroundColor(R.color.colorBlack)

        }
        else{
            v.background = null

        }
        //getListView()



        val filename = "sunnyside.txt"
        val fileContents = device.name + "/" + device.address
        this?.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it?.write(fileContents.toByteArray())
        }



/*        val file:String = "sunnyside.txt"
        val data:String = device.name
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
        }catch (e: Exception){
            e.printStackTrace()

        }*/




        if (mScanning) {
            mBluetoothLeScanner!!.stopScan(scanCallback)
           // mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
            mScanning = false
        }
         startActivity(intent)//DeviceControl로 넘어가는 코드
    }





    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler!!.postDelayed({
                mScanning = false
                mBluetoothLeScanner!!.stopScan(scanCallback)
               // mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                invalidateOptionsMenu()
            }, SCAN_PERIOD)

            mScanning = true
            mBluetoothLeScanner!!.startScan(scanCallback)
           // mBluetoothAdapter!!.startLeScan(mLeScanCallback)
        } else {
            mScanning = false
            mBluetoothLeScanner!!.stopScan(scanCallback)
           // mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
        }
        invalidateOptionsMenu()
    }

    // Adapter for holding devices found through scanning.
    private inner class LeDeviceListAdapter : BaseAdapter() {
        private val mLeDevices: ArrayList<BluetoothDevice>
        private val mInflator: LayoutInflater

        init {
            mLeDevices = ArrayList()
            mInflator = this@DeviceScanActivity.layoutInflater
        }

        fun addDevice(device: BluetoothDevice) {
            if (!mLeDevices.contains(device)) {
                if(device.name!=null){
                    if(device.name.contains("SUNNY")) {
                        mLeDevices.add(device)
                    }
                //이름 없는 디바이스들은 리스트에 추가하지 않겠음
                }
            }
        }

        fun getDevice(position: Int): BluetoothDevice? {
            return mLeDevices[position]
        }

        fun clear() {
            mLeDevices.clear()
        }

        override fun getCount(): Int {
            return mLeDevices.size
        }

        override fun getItem(i: Int): Any {
            return mLeDevices[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var view = view
            val viewHolder: ViewHolder
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null)
                viewHolder = ViewHolder()
                viewHolder.deviceAddress = view!!.findViewById<View>(R.id.device_address) as TextView
                viewHolder.deviceName = view.findViewById<View>(R.id.device_name) as TextView
                view.tag = viewHolder
            } else {
                viewHolder = view.tag as ViewHolder
            }

            val device = mLeDevices[i]
            val deviceName = device.name
            if (deviceName != null && deviceName.length > 0)
                viewHolder.deviceName!!.text = deviceName
            else
                viewHolder.deviceName!!.setText(R.string.unknown_device)
            viewHolder.deviceAddress!!.text = device.address

            return view
        }
    }

    internal class ViewHolder {
        var deviceName: TextView? = null
        var deviceAddress: TextView? = null
    }

    companion object {

        private val REQUEST_ENABLE_BT = 1
        // Stops scanning after 10 seconds.
        private val SCAN_PERIOD: Long = 10000

        val EXTRAS_ACTIVITY_NAME = ""
    }
}