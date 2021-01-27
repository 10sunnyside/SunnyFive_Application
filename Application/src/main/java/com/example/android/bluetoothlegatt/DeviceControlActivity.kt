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
import android.animation.Animator
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.view.View.*
import android.widget.*
import android.widget.ExpandableListView.GONE
import android.widget.ExpandableListView.OnChildClickListener
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android.bluetoothlegatt.SampleGattAttributes.DAYCYCLE
import com.example.android.bluetoothlegatt.SampleGattAttributes.DAYTIME
import kotlinx.android.synthetic.main.gatt_services_characteristics.*
import kotlinx.android.synthetic.main.sunnyside_main.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.android.bluetoothlegatt.SampleGattAttributes.INTENSITY
import com.example.android.bluetoothlegatt.SampleGattAttributes.LATITUDE
import io.github.stack07142.discreteseekbar.DiscreteSeekBar
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.colorpicker.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.coroutines.Runnable
import java.io.File
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with `BluetoothLeService`, which in turn interacts with the
 * Bluetooth LE API.
 */



class DeviceControlActivity : Activity() {

    private val REQUEST_ACCESS_FINE_LOCATION = 1000

    //var mConnected:Boolean? = false
    private var mPage_main: View? = null
    private var mPage_setting: View? = null
    private var mPage_light: View? = null
    private var mPage_scenario: View? = null
    private var mPage_alarm: View? = null
    private var mPage_sun: View? = null
    private var mPage_vitaminD: View? = null
    private var mPage_sky: View? = null
    private var mPage_demo: View? = null
    private var mPage_view: View? = null
    private var mPage_samsungcnt: View? = null
    private var mConnectionState: TextView? = null
    private var mDataField: TextView? = null
    private var mDeviceName: String? = null
    private var mDeviceAddress: String? = null
    private var mDeviceNames = arrayOfNulls<String>(10)
    private var mDeviceAddresses = arrayOfNulls<String>(10)

    private var mGattServicesList: ExpandableListView? = null
    private var mGattServicesList2: ExpandableListView? = null
    private var mBluetoothLeService: BluetoothLeService? = null
    private var mBluetoothLeService2: BluetoothLeService? = null
    private var mGattCharacteristics: ArrayList<ArrayList<BluetoothGattCharacteristic>>? = ArrayList()
    private var mGattCharacteristics2: ArrayList<ArrayList<BluetoothGattCharacteristic>>? = ArrayList()

    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null
    private var mNotifyCharacteristic2: BluetoothGattCharacteristic? = null

    private val LIST_NAME = "NAME"
    private val LIST_UUID = "UUID"
    private var gattServiceData: ArrayList<HashMap<String, String>>? = ArrayList()
    private var gattServiceData2: ArrayList<HashMap<String, String>>? = ArrayList()
    private var gattCharacteristicData: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
    private var gattCharacteristicData2: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
    val intentMain = Intent(this, MainActivity::class.java)


    private lateinit var mRandom: Random
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable


    private var URED:Int = 0
    private var UGREEN:Int = 0
    private var UBLUE:Int = 0
    private var UALPHA:Int = 0
    private var DRED:Int = 0
    private var DGREEN:Int = 0
    private var DBLUE:Int = 0
    private var DALPHA:Int = 0
    private var upperlower:Boolean? = null
    private var upperColor:Int = Color.WHITE
    private var lowerColor:Int = Color.WHITE

    public var checkMenu:Int = 0


    public var leftCheck:Int = 0
    public var rightCheck:Int = 0
    val handler = Handler()
    val millisTime:Long = 3000

    public var demoBrightness :Int = 0
    public var demoTemperature :Int = 0
    public var demoColorCount : Int = 0
    var demoTask = object : Runnable {
        override fun run() {
            // do task
            writeDemoColor(demoColorCount)
            demoColorCount++
            turnOnBackLight()

            if(rightCheck<12){
                if(rightCheck==11){
                    rightCheck++
                }
                else {
                    goRight(4)
                    rightCheck++
                    Thread.sleep(200)
                }
            }
            else if( (rightCheck>=12) && (leftCheck < 12) ){
                rightCheck++
                if(leftCheck==11){
                    leftCheck++
                }
                else {
                    goLeft(4)
                    leftCheck++
                    Thread.sleep(200)
                }
            }
            if(rightCheck==1){//7시
                writeTemperature(30)
            }
            else if(rightCheck==2){//8시
                writeTemperature(40)
            }
            else if(rightCheck==3){//9시
                writeTemperature(45)
            }
            else if(rightCheck==4){//10시
                writeBrightness(65)
                Thread.sleep(200)
                writeTemperature(50)
            }
            else if(rightCheck==5){//11시
                writeBrightness(75)
                Thread.sleep(200)
                writeTemperature(55)
            }
            else if(rightCheck==6){//12시
                writeBrightness(85)
                Thread.sleep(200)
                writeTemperature(58)
            }
            else if(rightCheck==7){//13시
                writeBrightness(95)
                Thread.sleep(200)
                writeTemperature(58)

            }
            else if(rightCheck==8){//14시
                writeBrightness(100)
                Thread.sleep(200)
                writeTemperature(58)
            }
            else if(rightCheck==9){//15시
                writeBrightness(85)
                Thread.sleep(200)
                writeTemperature(50)
            }
            else if(rightCheck==10){//16시
                writeBrightness(75)
                Thread.sleep(200)
                writeTemperature(40)
            }
            else if(rightCheck==11){//17시
                writeBrightness(85)
                Thread.sleep(200)
                writeTemperature(35)
            }
            else if(rightCheck==12){//18시
                writeBrightness(100)
                Thread.sleep(200)
                writeTemperature(27)
            }
            else if(leftCheck==1){//19시
                writeBrightness(80)
                Thread.sleep(200)
                writeTemperature(27)
            }
            else if(leftCheck==2){//20시
                writeBrightness(50)
            }
            else if(leftCheck==3){//21시
                writeBrightness(0)
            }
            else if(leftCheck==10){//4시
                writeBrightness(30)
                Thread.sleep(200)
                writeTemperature(65)
            }
            else if(leftCheck==11){//5시
                writeBrightness(30)
                Thread.sleep(200)
                writeTemperature(50)
            }
            else if(leftCheck==12){//6시
                writeBrightness(50)
                Thread.sleep(200)
                writeTemperature(27)
            }



            handler.postDelayed(this, millisTime) // millisTiem 이후 다시
        }
    }
    
    
    public var check:Int = 0

    public var daycyclevalue:Int? = 0
    public var alarmvalue:Int? = 0

    var testV:Int = 0
    var testStatus:Int = 0

    var testTimer:CountDownTimer?=null
    
    /*public var demoColorCodeUpRed:Array<Int> = arrayOf(203, 208, 213, 218, 221, 216, 205, 193, 193, 177, 149, 132, 116, 100, 84, 68, 51, 34, 23, 0, 0, 0, 0, 0)
    public var demoColorCodeUpGreen:Array<Int> = arrayOf(177, 161, 145, 129, 107, 96, 88, 72, 49, 40, 35, 26, 20, 24, 26, 27, 22, 15, 8, 0, 0, 4, 2, 0)
    public var demoColorCodeUpBlue:Array<Int> = arrayOf(219, 176, 128, 96, 76, 74, 73, 72, 18, 7, 3, 3, 3, 2, 1, 0, 0, 0, 0, 8, 6, 4, 2, 0)
    public var demoColorCodeDownRed:Array<Int> = arrayOf(134, 128, 120, 114, 106, 90, 74, 58, 45, 29, 14, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    public var demoColorCodeDownGreen:Array<Int> = arrayOf(177, 171, 166, 162, 158, 142, 126, 94, 49, 32, 15, 8, 0, 0, 0, 0, 0, 0, 0, 9, 9, 0, 0, 0)
    public var demoColorCodeDownBlue:Array<Int> = arrayOf(219, 216, 212, 208, 205, 189, 173, 157, 133, 133, 133, 85, 59, 43, 27, 23, 26, 8, 8, 8, 8, 5, 3, 0)
    */

    public var demoColorCodeUpRed:Array<Int> = arrayOf(125, 0, 0, 0, 0, 255, 0, 255, 167, 2, 0, 0, 0)
            public var demoColorCodeUpGreen:Array<Int> = arrayOf(41, 0, 0, 0, 255, 251, 55, 255, 0, 255, 0, 0, 0)
            public var demoColorCodeUpBlue:Array<Int> = arrayOf(98, 0, 23, 12, 255, 0, 0, 23, 137, 255, 0, 0, 0)
            public var demoColorCodeDownRed:Array<Int> = arrayOf(255, 88, 0, 169, 0, 88, 0, 116, 255, 202, 0, 0, 0)
            public var demoColorCodeDownGreen:Array<Int> = arrayOf(255, 0, 52, 124, 170, 0, 0, 228, 255, 255, 0, 0, 0)
            public var demoColorCodeDownBlue:Array<Int> = arrayOf(0, 0, 20, 20, 40, 138, 37, 192, 0, 88, 0, 0, 0)

    public var scntColorCodeUpRed:Array<Int> = arrayOf(125, 0, 0, 0, 0, 255, 0, 255, 167, 2, 0, 0, 0)
    public var scntColorCodeUpGreen:Array<Int> = arrayOf(41, 0, 0, 0, 255, 251, 55, 255, 0, 255, 0, 0, 0)
    public var scntColorCodeUpBlue:Array<Int> = arrayOf(98, 0, 23, 12, 255, 0, 0, 23, 137, 255, 0, 0, 0)
    public var scntColorCodeDownRed:Array<Int> = arrayOf(255, 88, 0, 169, 0, 88, 0, 116, 255, 202, 0, 0, 0)
    public var scntColorCodeDownGreen:Array<Int> = arrayOf(255, 0, 52, 124, 170, 0, 0, 228, 255, 255, 0, 0, 0)
    public var scntColorCodeDownBlue:Array<Int> = arrayOf(0, 0, 20, 20, 40, 138, 37, 192, 0, 88, 0, 0, 0)


            public var dailyColorCodeUpRed = intArrayOf(0, 0, 0, 0, 16, 149, 233, 125, 82, 82, 82, 82, 82, 82, 82, 203, 221, 193, 116, 34, 0, 0, 0, 0)
    public var dailyColorCodeUpGreen = intArrayOf(0, 0, 0, 0, 21, 148, 228, 226, 219, 219, 219, 219, 219, 219, 219, 177, 107, 49, 20, 15, 0, 4, 0, 0)
    public var dailyColorCodeUpBlue = intArrayOf(0, 0, 0, 0, 8, 28, 48, 255, 255, 255, 255, 255, 255, 255, 255, 219, 76, 18, 3, 0, 8, 4, 0, 0)
    public var dailyColorCodeDownRed = intArrayOf(0, 0, 0, 0, 16, 24, 50, 162, 162, 162, 162, 162, 162, 162, 162, 134, 106, 45, 0, 0, 0, 0, 0, 0)
    public var dailyColorCodeDownGreen = intArrayOf(0, 0, 0, 0, 16, 23, 75, 226, 226, 226, 226, 226, 226, 226, 226, 177, 158, 49, 0, 0, 9, 0, 0, 0)
    public var dailyColorCodeDownBlue = intArrayOf(0, 0, 0, 0, 16, 21, 72, 255, 255, 255, 255, 255, 255, 255, 255, 219, 205, 133, 59, 8, 8, 3, 0, 0)

            // Code to manage Service lifecycle.
    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService!!.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                finish()
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService!!.connect(mDeviceAddresses[0])

            //mBluetoothLeService2!!.connect2(mDeviceAddresses[1])
           // mBluetoothLeService!!.connect2(mDeviceAddresses[1])
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }

    private val mServiceConnection2 = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService2 = (service as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService2!!.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                finish()
            }
            // Automatically connects to the device upon successful start-up initialization.
            //mBluetoothLeService!!.connect(mDeviceAddresses[0])
            mBluetoothLeService2!!.connect2(mDeviceAddresses[1])
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService2 = null
        }
    }



    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothLeService.ACTION_GATT_CONNECTED == action) {
                mConnected = true
                updateConnectionState(R.string.connected)
                invalidateOptionsMenu()
                mPage_setting!!.visibility = INVISIBLE
                mPage_light!!.visibility = INVISIBLE
                mPage_scenario!!.visibility = INVISIBLE
                mPage_alarm!!.visibility = INVISIBLE
                mPage_sun!!.visibility = INVISIBLE
                mPage_vitaminD!!.visibility = INVISIBLE
                mPage_sky!!.visibility = INVISIBLE
                mPage_main!!.visibility = INVISIBLE
                colorSelector!!.visibility = INVISIBLE
                mPage_demo!!.visibility = INVISIBLE
                mPage_view!!.visibility = INVISIBLE
                mPage_samsungcnt!!.visibility = INVISIBLE
             //   startActivity(intentMain)




            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED == action) {
                mConnected = false
                updateConnectionState(R.string.disconnected)
                invalidateOptionsMenu()
                clearUI()
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED == action) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService!!.supportedGattServices)
                //displayGattServices(mBluetoothLeService!!.supportedGattServices2)
                //displayGattServices2(mBluetoothLeService2!!.supportedGattServices2)



            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE == action) {
                //Toast.makeText(applicationContext,"ACTION_DATA_AVAILABLE",Toast.LENGTH_SHORT).show()
                //val parts = stringBuilder.toString().split(delimiter)
                val data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA)

                val data2 = data.split("\n")

                if(INTENSITY.toString()==data2[0]){
                    text_brightness2.text = data2[2]
                    text_brightness.text = data2[2]
                    seekBar_brightness.progress = data2[2].toInt()
                }

                if(TEMPERATURE.toString()==data2[0]){
                    text_temperature2.text = data2[2]+"00 K"
                    text_temperature.text = data2[2]+"00 K"
                    seekBar_temperature.progress = data2[2].toInt()
                }

                if(UV.toString()==data2[0]){
                    text_uvb.text = data2[2]
                         seekBar_vitaminD.progress = data2[2].toInt()

                }

                if(LATITUDE.toString()==data2[0]){
                    text_angle2.text = data2[2]
                    //seekBar_angle.progress = data2[2].toInt()
                }

                if(DAYCYCLE.toString()==data2[0]){
                    daycyclevalue = Integer.parseInt(data2[2])
                   // seekBar_angle.progress = data2[2].substring(0,2).toInt()

                    if(daycyclevalue==0){
                        layout_daytime.setBackgroundColor(Color.WHITE)
                    }

                }

                if(DAYTIME.toString()==data2[0]){
                    if(daycyclevalue==1) {
                        text_daytime.text = data2[1]
                        layout_daytime.setBackgroundColor(Color.YELLOW)
                        //seekBar_angle.progress = data2[2].substring(0, 2).toInt()
                    }
                }
                if(ALARM.toString()==data2[0]){
                    alarmvalue = Integer.parseInt(data2[2])
                    if(alarmvalue==0){
                        layout_alarm.setBackgroundColor(Color.WHITE)
                    }
                }
                if(SETTIME.toString()==data2[0]){
                    if(alarmvalue!=0) {
                        text_alarm2.text = data2[1]
                        //text_alarm.text = data2[1]
                        layout_alarm.setBackgroundColor(Color.YELLOW)
                        //seekBar_angle.progress = data2[2].substring(0, 2).toInt()
                    }
                }
                if(CURRENT.toString()==data2[0]){

                    text_curtime.text = data2[1]

                        //seekBar_angle.progress = data2[2].substring(0, 2).toInt()
                }
                if(BLRU.toString()==data2[0]){
                    URED = Integer.parseInt(data2[2])
                }
                if(BLGU.toString()==data2[0]){
                    UGREEN = Integer.parseInt(data2[2])
                }
                if(BLBU.toString()==data2[0]){
                    UBLUE = Integer.parseInt(data2[2])
                }
                if(BLRD.toString()==data2[0]){
                    DRED = Integer.parseInt(data2[2])
                }
                if(BLGD.toString()==data2[0]){
                    DGREEN = Integer.parseInt(data2[2])
                }
                if(BLBD.toString()==data2[0]){
                    DBLUE = Integer.parseInt(data2[2])
                }

                upperColor = Color.rgb(URED,UGREEN,UBLUE)
                lowerColor = Color.rgb(DRED,DGREEN,DBLUE)
                var drawable = btnColorSelected.background as GradientDrawable
                var drawable2 = btnColorSelected2.background as GradientDrawable

                    drawable.colors=intArrayOf(upperColor, lowerColor)
                    drawable2.colors=intArrayOf(upperColor, lowerColor)

                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
            }
        }
    }


    private val mGattUpdateReceiver2 = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothLeService.ACTION_GATT_CONNECTED == action) {


                invalidateOptionsMenu()

                //   startActivity(intentMain)




            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED == action) {


            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED == action) {
                // Show all the supported services and characteristics on the user interface.

                //displayGattServices(mBluetoothLeService!!.supportedGattServices2)
                displayGattServices2(mBluetoothLeService2!!.supportedGattServices2)



            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE == action) {
                //Toast.makeText(applicationContext,"ACTION_DATA_AVAILABLE",Toast.LENGTH_SHORT).show()
                //val parts = stringBuilder.toString().split(delimiter)

            }
        }
    }




    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private val servicesListClickListner = OnChildClickListener { parent, v, groupPosition, childPosition, id ->
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![groupPosition][childPosition]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                        it, false)
                    mNotifyCharacteristic = null }
                mNotifyCharacteristic2?.let { mBluetoothLeService2!!.setCharacteristicNotification2(
                        it, false)
                    mNotifyCharacteristic2 = null }

                mBluetoothLeService!!.readCharacteristic(characteristic)
                characteristic.setValue("TT")
                mBluetoothLeService!!.writeCharacteristic(characteristic)
                Thread.sleep(100)
                mBluetoothLeService2!!.readCharacteristic2(characteristic)
                characteristic.setValue("TT")
                mBluetoothLeService2!!.writeCharacteristic2(characteristic)
            }
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                mNotifyCharacteristic = characteristic
                mBluetoothLeService!!.setCharacteristicNotification(
                        characteristic, true)
                mNotifyCharacteristic2 = characteristic
                mBluetoothLeService2!!.setCharacteristicNotification2(
                        characteristic, true)
            }
            return@OnChildClickListener true
        }
        false
    }

    private fun clearUI() {
        mGattServicesList!!.setAdapter(null as SimpleExpandableListAdapter?)
        mDataField!!.setText(R.string.no_data)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gatt_services_characteristics)


       // mDeviceNames = arrayOfNulls<String>(10)
      //  mDeviceAddresses = arrayOfNulls<String>(10)






        mPage_main = findViewById(R.id.page_main);
        mPage_setting = findViewById(R.id.page_setting)
        mPage_light = findViewById(R.id.page_light);
        mPage_scenario = findViewById(R.id.page_scenario);
        mPage_alarm = findViewById(R.id.page_alarm);
        mPage_sun = findViewById(R.id.page_sun);
        mPage_vitaminD = findViewById(R.id.page_vitaminD);
        mPage_sky = findViewById(R.id.page_sky);
        mPage_demo = findViewById(R.id.page_demo)
        mPage_view = findViewById(R.id.page_view)
        mPage_samsungcnt = findViewById(R.id.page_samsungcnt)

        this.mPage_setting!!.visibility = INVISIBLE
        this.mPage_light!!.visibility = INVISIBLE
        this.mPage_scenario!!.visibility = INVISIBLE
        this.mPage_alarm!!.visibility = INVISIBLE
        this.mPage_sun!!.visibility = INVISIBLE
        this.mPage_vitaminD!!.visibility = INVISIBLE
        this.mPage_sky!!.visibility = INVISIBLE
        this.mPage_main!!.visibility = INVISIBLE
        this.mPage_demo!!.visibility = INVISIBLE
        this.mPage_view!!.visibility = INVISIBLE
        this.mPage_samsungcnt!!.visibility = INVISIBLE
        val intent = intent

        //mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME)
        //mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS)
        // 1개만 연결할 때 쓰는 코드

        for(i in DEVICE_NAMES.indices){
            //if()
            this!!.mDeviceNames!![i] = intent.getStringExtra(DEVICE_NAMES[i])
            }
        for(i in DEVICE_ADDRESSES.indices){
            this!!.mDeviceAddresses!![i] = intent.getStringExtra(DEVICE_ADDRESSES[i])
        } //여기까지


        // Sets up UI references.
        (findViewById<View>(R.id.device_address) as TextView).text = mDeviceAddress
        mGattServicesList = findViewById<View>(R.id.gatt_services_list) as ExpandableListView
        mGattServicesList!!.setOnChildClickListener(servicesListClickListner)
        mConnectionState = findViewById<View>(R.id.connection_state) as TextView
        mDataField = findViewById<View>(R.id.data_value) as TextView

        actionBar!!.title = mDeviceName
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        //actionBar!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        //actionBar!!.setBackgroundDrawable(ColorDrawable(Color.rgb(255,221,217)))

        //메뉴바 색상 변경

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        val gattServiceIntent2 = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        bindService(gattServiceIntent2, mServiceConnection2, Context.BIND_AUTO_CREATE)

        mRandom = Random()
        mHandler = Handler()


        changeLanguage()
        //var intentbr = Intent(this.packageName)


        val tickMarkTextArr1 = SparseArray<String>()
        tickMarkTextArr1.append(-400, "일출")
        tickMarkTextArr1.append(-200, "아침")
        tickMarkTextArr1.append(0, "한낮")
        tickMarkTextArr1.append(200, "저녁")
        tickMarkTextArr1.append(400, "일몰")

        slider_1.getConfigBuilder()
                .setTickMarkTextArray(tickMarkTextArr1)
                //.setTrackColor(R.color.colorLime400)
                //.setTrackWidth(5)
                .setOnValueChangedListener(object : DiscreteSeekBar.OnValueChangedListener {
                    override fun onValueChanged(value: Int) {
                        Toast.makeText(applicationContext, "value= $value", Toast.LENGTH_SHORT).show()
                    }
                })
                .build()

        val tickMarkTextArr2 = SparseArray<String>()
        tickMarkTextArr2.append(0, "좌")

        tickMarkTextArr2.append(40, "우")

        seekBar_latitude.getConfigBuilder()
                .setTickMarkTextArray(tickMarkTextArr2)
                .setOnValueChangedListener(object : DiscreteSeekBar.OnValueChangedListener {
                    override fun onValueChanged(value: Int) {
                        latitude(value)
                        Toast.makeText(applicationContext, "value= $value", Toast.LENGTH_SHORT).show()
                    }
                })
                .build()

        val tickMarkTextArr3 = SparseArray<String>()
        tickMarkTextArr3.append(6, "일출")
        tickMarkTextArr3.append(9, "아침")
        tickMarkTextArr3.append(12, "한낮")
        tickMarkTextArr3.append(15, "저녁")
        tickMarkTextArr3.append(18, "일몰")

        seekBar_sunnyside.getConfigBuilder()
                .setTickMarkTextArray(tickMarkTextArr3)
                .setOnValueChangedListener(object : DiscreteSeekBar.OnValueChangedListener {
                    override fun onValueChanged(value: Int) {
                        test(value)
                        Toast.makeText(applicationContext, "value= $value", Toast.LENGTH_SHORT).show()
                    }
                })
                .build()



       // val drawable = AppCompatResources.getDrawable(this,R.drawable.ic_sunny)
       /* light.compoundDrawables.get(1).setColorFilter(Color.rgb(242,255,201),PorterDuff.Mode.SRC_IN)
        scenario.compoundDrawables.get(1).setColorFilter(Color.rgb(242,255,201),PorterDuff.Mode.SRC_IN)
        alarm.compoundDrawables.get(1).setColorFilter(Color.rgb(242,255,201),PorterDuff.Mode.SRC_IN)
        sun.compoundDrawables.get(1).setColorFilter(Color.rgb(242,255,201),PorterDuff.Mode.SRC_IN)
        vitaminD.compoundDrawables.get(1).setColorFilter(Color.rgb(242,255,201),PorterDuff.Mode.SRC_IN)
        sky.compoundDrawables.get(1).setColorFilter(Color.rgb(242,255,201),PorterDuff.Mode.SRC_IN)*/

        // SVG 색상 변경

        testTimer = object: CountDownTimer(600000,1000){

            override fun onTick(millisUntilFinished: Long) {

                text_testTimer.text= (millisUntilFinished/1000).toString() + " 초"

            }

            override fun onFinish() {

                mRunnable = Runnable {
                    testerVersionOff()
                    display_dashboard()
                    display_dashboard2()

                }
                testStatus = 0
                testVersion.setBackgroundColor(Color.GRAY)
                testVersion.setTextColor(Color.BLACK)

                // mHandler.post(mRunnable)
                val thread = Thread(mRunnable)
                thread.start()

            }
        }



        button_reset.setOnClickListener {

            if (mGattCharacteristics != null) {
                val characteristic = mGattCharacteristics!![7][5]
                val charaProp = characteristic.properties
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                    /* mNotifyCharacteristic?.let {
                         mBluetoothLeService!!.setCharacteristicNotification(
                                 it, false)
                         mNotifyCharacteristic = null
                     }
*/
                    // mBluetoothLeService!!.readCharacteristic(characteristic)
                    characteristic.setValue(0, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                    mBluetoothLeService!!.writeCharacteristic2(characteristic)

                }
                /*if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    mNotifyCharacteristic = characteristic
                    mBluetoothLeService!!.setCharacteristicNotification(
                            characteristic, true)
                }*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
            }
        }

        button_write.setOnClickListener {

            if (mGattCharacteristics != null) {
                val characteristic = mGattCharacteristics!![7][6]
                val charaProp = characteristic.properties
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                    /* mNotifyCharacteristic?.let {
                         mBluetoothLeService!!.setCharacteristicNotification(
                                 it, false)
                         mNotifyCharacteristic = null
                     }
*/
                    // mBluetoothLeService!!.readCharacteristic(characteristic)
                    characteristic.setValue(0, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                    mBluetoothLeService!!.writeCharacteristic2(characteristic)

                }
                /*if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    mNotifyCharacteristic = characteristic
                    mBluetoothLeService!!.setCharacteristicNotification(
                            characteristic, true)
                }*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
            }
        }





        //시나리오 color, scenario color
        sunsetpurple.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(0)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }
        sunsetred.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(1)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }
        greenblueblack.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(2)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }
        yellowblack.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(3)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }
        seablue.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(4)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }
        sunsetorange.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(5)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }
        greenblue.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(6)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }
        plane.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(7)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }
        sunsetorangepurple.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(8)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }
        sky2.setOnClickListener {
            mRunnable = Runnable {
                writeDemoColor(9)
                turnOnBackLight()
            }
            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }






        //test Dr.Oh
        //testerVersion()

        text_open_page_light_s.setOnClickListener {

            if(checkMenu==0) {
                page_light_s.visibility = VISIBLE
                expand_more.visibility = GONE
                expand_less.visibility = VISIBLE

                checkMenu = 1
            }
            else{
                page_light_s.visibility = GONE
                expand_more.visibility = VISIBLE
                expand_less.visibility = GONE
                checkMenu = 0
            }
        }



        testVersion.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)

            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()


            val k = builder.setView(dialogView).show()







            //버튼 색과 폰트 바꾸기
            //2700 6500 사이인 4600으로 설정
            //밝기 최대

            if (testStatus==0) {
                mRunnable = Runnable {
                    testerVersionOn()
                    display_dashboard()
                    k.dismiss()
                }
                testStatus = 1
                testVersion.setBackgroundColor(Color.YELLOW)
                testVersion.setTextColor(Color.BLUE)

                // mHandler.post(mRunnable)
                val thread = Thread(mRunnable)
                thread.start()
                (testTimer as CountDownTimer).start()

            }
            else if (testStatus==1) {
                mRunnable = Runnable {
                    testerVersionOff()
                    display_dashboard()
                    k.dismiss()

                }
                testStatus = 0
                testVersion.setBackgroundColor(Color.GRAY)
                testVersion.setTextColor(Color.BLACK)

                // mHandler.post(mRunnable)
                val thread = Thread(mRunnable)
                thread.start()
                (testTimer as CountDownTimer).cancel()
            }
        }

        //setUpAnimation(lottieAnimView)

/*        val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
        animationView.setAnimation("8661-rotating-sun.json")
        animationView.loop(true)
        animationView.playAnimation()
        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                animationView.visibility=View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })*/

        //public var progressDialog : AppCompatDialog? = null


  /*      val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


        val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
        //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
        animationView.setAnimation("8661-rotating-sun.json")
        animationView.loop(true)
        animationView.playAnimation()*/






        //builder.setCancelable(false)
        //animationView.scale = 0.3f


        //builder.setView(dialogView).show()




/*        left_arrow.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN ->{


                        mRunnable = Runnable {
                            goLeft()
                            check=0
                        }


                        // mHandler.post(mRunnable)
                        val thread = Thread(mRunnable)
                        thread.start()

                    }


                MotionEvent.ACTION_UP -> {
                    goStop()
                    }

                //Do Something
            }

            v?.onTouchEvent(event) ?: true
        }
        right_arrow.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN ->{

                        mRunnable = Runnable {
                            goRight()
                        }


                        // mHandler.post(mRunnable)
                        val thread = Thread(mRunnable)
                        thread.start()

                    }


                MotionEvent.ACTION_UP -> {
                    goStop()

                }

                //Do Something
            }

            v?.onTouchEvent(event) ?: true
        }*/


        left_arrow.setOnClickListener {
            mRunnable = Runnable {
                goLeft(4)
            }


            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()

        }

        right_arrow.setOnClickListener {
            mRunnable = Runnable {
                goRight(4)
            }


            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()

        }

        left_arrow.setOnLongClickListener {
            mRunnable = Runnable {
                goLeftStraight()
            }


            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
            true

        }

        right_arrow.setOnLongClickListener {
            mRunnable = Runnable {
                goRightStraight()
            }


            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
            true
        }

        light.setOnClickListener {
            this.mPage_main!!.visibility = INVISIBLE
            this.mPage_light!!.visibility = VISIBLE


         /*   if (mGattCharacteristics != null) {
                val characteristic = mGattCharacteristics!![2][4]
                val charaProp = characteristic.properties
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }
                    mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                            it, false)
                        mNotifyCharacteristic = null }


                    // mBluetoothLeService!!.readCharacteristic(characteristic)

                      characteristic.setValue(i,BluetoothGattCharacteristic.FORMAT_UINT16,0)
                      mBluetoothLeService!!.writeCharacteristic(characteristic)
                }
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    mNotifyCharacteristic = characteristic
                    mBluetoothLeService!!.setCharacteristicNotification(
                            characteristic, true)
                }
                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE
                return
            }
            //    mPage_setting!!.visibility = VISIBLE
            //    mPage_light!!.visibility = INVISIBLE*/



/*            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {
                        if (INTENSITY == row[columnIndex].uuid) {
                            //return Point(rowIndex, columnIndex)
                            // value 에 ? 혹은 if 문으로 null체크
                            text_brightness.text = row[columnIndex].value[0]?.toString()
                             Toast.makeText(applicationContext,row[columnIndex].value[0]?.toString(),Toast.LENGTH_SHORT).show()

                        }
                        if (INTENSITY == row[columnIndex].uuid) {
                            //return Point(rowIndex, columnIndex)
                            // value 에 ? 혹은 if 문으로 null체크
                            text_brightness.text = row[columnIndex].value?.toString()
                            Toast.makeText(applicationContext,row[columnIndex].value?.toString(),Toast.LENGTH_SHORT).show()

                        }

                        if (TEMPERATURE == row[columnIndex].uuid) {
                            //return Point(rowIndex, columnIndex)
                            // value 에 ? 혹은 if 문으로 null체크
                            text_temperature.text = row[columnIndex].value?.toString()
                            Toast.makeText(applicationContext,row[columnIndex].value?.toString(),Toast.LENGTH_SHORT).show()

                        }

                    }
                }
            }*/


            if (mGattCharacteristics != null) {

                for (rowIndex in mGattCharacteristics!!.indices) {
                    val row = mGattCharacteristics!![rowIndex]
                    if (row != null) {
                        for (columnIndex in row.indices) {
                            if (INTENSITY == row[columnIndex].uuid) {
                                val characteristic = row[columnIndex]
                                val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                                    mNotifyCharacteristic?.let {
                                        mBluetoothLeService!!.setCharacteristicNotification(
                                                it, false)
                                        mNotifyCharacteristic = null
                                    }
                                    mBluetoothLeService!!.readCharacteristic(characteristic)
                                    Thread.sleep(100)
                                    mNotifyCharacteristic2?.let {
                                        mBluetoothLeService2!!.setCharacteristicNotification2(
                                                it, false)
                                        mNotifyCharacteristic2 = null
                                    }
                                    mBluetoothLeService2!!.readCharacteristic2(characteristic)
                                    Thread.sleep(100)
                                }
                            }


                            if (TEMPERATURE == row[columnIndex].uuid) {
                                val characteristic = row[columnIndex]
                                val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                                    mNotifyCharacteristic?.let {
                                        mBluetoothLeService!!.setCharacteristicNotification(
                                                it, false)
                                        mNotifyCharacteristic = null
                                    }
                                    mBluetoothLeService!!.readCharacteristic(characteristic)
                                    Thread.sleep(100)
                                }
                            }
                            if (LATITUDE == row[columnIndex].uuid) {
                                val characteristic = row[columnIndex]
                                val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                                    mNotifyCharacteristic?.let {
                                        mBluetoothLeService!!.setCharacteristicNotification(
                                                it, false)
                                        mNotifyCharacteristic = null
                                    }
                                    mBluetoothLeService!!.readCharacteristic(characteristic)
                                    Thread.sleep(100)
                                }
                            }
                        }
                    }
                }
/*                val characteristic = mGattCharacteristics!![4][1]
                val charaProp = characteristic.properties
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
                    mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                            it, false)
                        mNotifyCharacteristic = null }

                    // mBluetoothLeService!!.readCharacteristic(characteristic)
                   // characteristic.setValue(localTime2,BluetoothGattCharacteristic.FORMAT_UINT16,0)
                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                }
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    mNotifyCharacteristic = characteristic
                    mBluetoothLeService!!.setCharacteristicNotification(
                            characteristic, true)
                }*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
            }




        }

        scenario.setOnClickListener {
            this.mPage_main!!.visibility = INVISIBLE
            //this.mPage_scenario!!.visibility = VISIBLE
            //시나리오 page view로 변경
            this.mPage_view!!.visibility = VISIBLE


            if (mGattCharacteristics != null) {

                for (rowIndex in mGattCharacteristics!!.indices) {
                    val row = mGattCharacteristics!![rowIndex]
                    if (row != null) {
                        for (columnIndex in row.indices) {
                            if (DAYCYCLE == row[columnIndex].uuid) {
                                val characteristic = row[columnIndex]
                                val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                                    mNotifyCharacteristic?.let {
                                        mBluetoothLeService!!.setCharacteristicNotification(
                                                it, false)
                                        mNotifyCharacteristic = null
                                    }
                                    mBluetoothLeService!!.readCharacteristic(characteristic)
                                    Thread.sleep(100)
                                }
                            }

                            if (DAYTIME == row[columnIndex].uuid) {
                                val characteristic = row[columnIndex]
                                val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                                    mNotifyCharacteristic?.let {
                                        mBluetoothLeService!!.setCharacteristicNotification(
                                                it, false)
                                        mNotifyCharacteristic = null
                                    }
                                    mBluetoothLeService!!.readCharacteristic(characteristic)
                                    Thread.sleep(100)
                                }
                            }

                        }
                    }
                }
            }





        }

        alarm.setOnClickListener {
            this.mPage_main!!.visibility = INVISIBLE
            this.mPage_alarm!!.visibility = VISIBLE


            val format1 = SimpleDateFormat("HH")
            val format2 = SimpleDateFormat("mm")
            val format3 = SimpleDateFormat("ss")

            val tz = TimeZone.getTimeZone("Asia/Seoul")

            val gc = GregorianCalendar(tz)

/*
            var year = gc.get(GregorianCalendar.YEAR).toString()

            var month = gc.get(GregorianCalendar.MONTH).toString()

            var day = gc.get(GregorianCalendar.DATE).toString()

            var hour= gc.get(GregorianCalendar.HOUR).toString()

            var min = gc.get(GregorianCalendar.MINUTE).toString()

            var sec = gc.get(GregorianCalendar.SECOND).toString()
*/



            var hour = format1.format(gc.time)
            var minute = format2.format(gc.time)
            var second = format3.format(gc.time)
/*            if(Integer.parseInt(hour)<9){
                hour="0"+hour
            }
            if(Integer.parseInt(minute)<9){
                minute="0"+minute

            }*/

            var hourInt= Integer.parseInt(hour)
            var minuteInt= Integer.parseInt(minute)
            var secondInt = Integer.parseInt(second)
            hourInt = hourInt
            hourInt = (hourInt/10 shl 4) + (hourInt % 10)
            minuteInt = (minuteInt/10 shl 4) + (minuteInt % 10)
            secondInt = (secondInt/10 shl 4) + (secondInt % 10)

            secondInt = secondInt shl 16
            minuteInt = minuteInt shl 8
            var localTimeInt = hourInt+minuteInt+secondInt

            var localTime = hour + minute + second

            var localTime2 = Integer.parseInt(localTime)


            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()


            val k = builder.setView(dialogView).show()

            mRunnable = Runnable {

                if (mGattCharacteristics != null) {
                    val characteristic = mGattCharacteristics!![4][1]
                    val charaProp = characteristic.properties
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
/*                    mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                            it, false)
                        mNotifyCharacteristic = null }*/

                        // mBluetoothLeService!!.readCharacteristic(characteristic)
                        characteristic.setValue(localTimeInt, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }
/*                if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    mNotifyCharacteristic = characteristic
                    mBluetoothLeService!!.setCharacteristicNotification(
                            characteristic, true)
                }*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
                }
                k.dismiss()
            }


            val thread = Thread(mRunnable)
            thread.start()


        }

        sun.setOnClickListener {
            this.mPage_main!!.visibility = INVISIBLE
            this.mPage_sun!!.visibility = VISIBLE

        }

        vitaminD.setOnClickListener {
            this.mPage_main!!.visibility = INVISIBLE
            this.mPage_vitaminD!!.visibility = VISIBLE

        }

        sky.setOnClickListener {
            this.mPage_main!!.visibility = INVISIBLE
            this.mPage_sky!!.visibility = VISIBLE
        }


        dashboard.setOnClickListener{
            this.mPage_main!!.visibility = INVISIBLE
            this.mPage_demo!!.visibility = VISIBLE
        }
        demo2.setOnClickListener{
            this.mPage_main!!.visibility = INVISIBLE
            this.mPage_demo!!.visibility = VISIBLE
        }

       /* color_sky.setOnClickListener {
        //    val id = it.id



        }*/


        button_demo.setOnClickListener {


            val demoState = button_demo.text

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()


            val k = builder.setView(dialogView).show()

            mRunnable = Runnable {
                for (rowIndex in mGattCharacteristics!!.indices) {
                    val row = mGattCharacteristics!![rowIndex]
                    if (row != null) {
                        for (columnIndex in row.indices) {
                            if (DAYCYCLE == row[columnIndex].uuid) {
                                val characteristic = row[columnIndex]
                                val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                                    /*mNotifyCharacteristic?.let {
                                        mBluetoothLeService!!.setCharacteristicNotification(
                                                it, false)
                                        mNotifyCharacteristic = null
                                    }*/
                                    if(demoState=="Demo On") {
                                        characteristic.setValue(255, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                                        button_demo.text = "Demo Off"
                                    }
                                    else{
                                        characteristic.setValue(0, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                                        button_demo.text = "Demo On"
                                    }
                                }
                                /*if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                                    mNotifyCharacteristic = characteristic
                                    mBluetoothLeService!!.setCharacteristicNotification(
                                            characteristic, true)
                                }*/

                                //mBluetoothLeService!!.readCharacteristic(characteristic)
                                Thread.sleep(100)
                            }
                        }
                    }
                }
                k.dismiss()


            }

            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()



            /* 기존 소프트웨어 데모, 살리면 사용 가능
            leftCheck = 0
            rightCheck = 0
            demoBrightness = 0
            demoTemperature = 0
            goLeftStraight()
            Thread.sleep(200)
            writeBrightness(50)
            Thread.sleep(200)
            writeTemperature(27)

            Thread.sleep(5000)



            handler.post(demoTask) // tick timer 실행

*/


        }

        page_daylight.setOnClickListener {
            slider_1.visibility = VISIBLE
            button_day.visibility = VISIBLE
            button_xday.visibility = VISIBLE
            //page_sunset.visibility = View.GONE
           // page_dawn.visibility = GONE

        }

/*        page_mood.setOnClickListener{
           // page_sunset.visibility = VISIBLE
           // page_dawn.visibility = VISIBLE
            slider_1.visibility = GONE
            button_day.visibility = GONE
            button_xday.visibility = GONE
        }*/



/*        page_sunset.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()


            val k = builder.setView(dialogView).show()

            mRunnable = Runnable {
                test()
                k.dismiss()


            }
            page_mood.setBackgroundColor(Color.rgb(255,165,0))
            page_sunset.setBackgroundColor(Color.rgb(255,165,0))
            page_dawn.setBackgroundColor(Color.WHITE)
            page_daylight.setBackgroundColor(Color.WHITE)

           // mHandler.post(mRunnable)
           val thread = Thread(mRunnable)
            thread.start()
        }*/




    /*    page_dawn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()
            val k = builder.setView(dialogView).show()

            mRunnable = Runnable {

                if (mGattCharacteristics != null) {
                    val characteristic = mGattCharacteristics!![3][1]
                    val charaProp = characteristic.properties

                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
*//*                                        mNotifyCharacteristic?.let {
                                            mBluetoothLeService!!.setCharacteristicNotification(
                                                    it, false)
                                            mNotifyCharacteristic = null
                                        }*//*

                        characteristic.setValue(23, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                        text_daytime.text = "OFF"

                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }
*//*                                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                                        mNotifyCharacteristic = characteristic
                                        mBluetoothLeService!!.setCharacteristicNotification(
                                                characteristic, true)
                                    }*//*

                    //mBluetoothLeService!!.readCharacteristic(characteristic)
                    Thread.sleep(200)
                }

                if (mGattCharacteristics != null) {

                    val characteristic = mGattCharacteristics!![3][0]
                    val charaProp = characteristic.properties

                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
                        *//* mNotifyCharacteristic?.let {
                                            mBluetoothLeService!!.setCharacteristicNotification(
                                                    it, false)
                                            mNotifyCharacteristic = null
                                        }*//*

                        characteristic.setValue(254, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }
                    *//*if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                                        mNotifyCharacteristic = characteristic
                                        mBluetoothLeService!!.setCharacteristicNotification(
                                                characteristic, true)
                                    }*//*

                    //mBluetoothLeService!!.readCharacteristic(characteristic)
                    Thread.sleep(100)
                }


            k.dismiss()
            }


            //mHandler.post(mRunnable)

            val thread = Thread(mRunnable)
            thread.start()

            page_mood.setBackgroundColor(Color.rgb(255, 165, 0))
            page_dawn.setBackgroundColor(Color.rgb(255, 165, 0))
            page_sunset.setBackgroundColor(Color.WHITE)
            page_daylight.setBackgroundColor(Color.WHITE)
        }*/




        button_alarmx.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()
            val k = builder.setView(dialogView).show()

            mRunnable = Runnable {
            // Day Cycle 0 to 1
            if (mGattCharacteristics != null) {
                val characteristic = mGattCharacteristics!![4][0]
                val charaProp = characteristic.properties
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
/*                    mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                            it, false)
                        mNotifyCharacteristic = null }*/

                    // mBluetoothLeService!!.readCharacteristic(characteristic)
                    characteristic.setValue(0,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                }
/*                if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    mNotifyCharacteristic = characteristic
                    mBluetoothLeService!!.setCharacteristicNotification(
                            characteristic, true)
                }*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
            }
            // Day Time Setting
            text_alarm.text=""

                k.dismiss()
            }


            //mHandler.post(mRunnable)

            val thread = Thread(mRunnable)
            thread.start()

        }



        button_alarm.setOnClickListener {
            var hour = timePicker_alarm.hour
            var minute = timePicker_alarm.minute

/*            if (Integer.parseInt(hour) < 9) {
                hour = "0" + hour
            }
            if (Integer.parseInt(minute) < 9) {
                minute = "0" + minute

            }*/

            var msg = hour.toString() + "m : " + minute.toString()



            hour = (hour/10 shl 4) + (hour % 10)
            minute = (minute/10 shl 4) + (minute % 10)

             minute = minute shl 8

            var alarmTime = hour + minute
           // var alarmTime2 = Integer.parseInt(alarmTime)

            val checkedAlarmId = radioGroup_alarm.checkedRadioButtonId
            val checkedAlarm:RadioButton = findViewById(checkedAlarmId)
            val checkedRepeat = checkBox_repeat.isChecked


            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()


            val k = builder.setView(dialogView).show()

            mRunnable = Runnable {



                // Day Time Setting
                if (mGattCharacteristics != null) {
                    val characteristic = mGattCharacteristics!![4][2]
                    val charaProp = characteristic.properties
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                       /* mNotifyCharacteristic?.let {
                            mBluetoothLeService!!.setCharacteristicNotification(
                                    it, false)
                            mNotifyCharacteristic = null
                        }
*/
                        // mBluetoothLeService!!.readCharacteristic(characteristic)
                        characteristic.setValue(alarmTime, BluetoothGattCharacteristic.FORMAT_UINT16, 0)
                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }
                    /*if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
                    Thread.sleep(200)

                }

                // Day Cycle 0 to 1
                if (mGattCharacteristics != null) {
                    val characteristic = mGattCharacteristics!![4][0]
                    val charaProp = characteristic.properties
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                        /*mNotifyCharacteristic?.let {
                            mBluetoothLeService!!.setCharacteristicNotification(
                                    it, false)
                            mNotifyCharacteristic = null
                        }*/
                                Log.d("check",checkedAlarm.id.toString())
                        // mBluetoothLeService!!.readCharacteristic(characteristic)
                        if((radioButton_alarmOn.isChecked == true) && (checkedRepeat == true)) {
                            characteristic.setValue(17, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                            mBluetoothLeService!!.writeCharacteristic(characteristic)
                        }
                        else if((radioButton_alarmOff.isChecked == true) && (checkedRepeat == true)) {
                            characteristic.setValue(18, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                            mBluetoothLeService!!.writeCharacteristic(characteristic)
                        }
                        else if((radioButton_alarmOn.isChecked == true) && (checkedRepeat == false)) {
                            characteristic.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                            mBluetoothLeService!!.writeCharacteristic(characteristic)
                        }
                        else if((radioButton_alarmOff.isChecked == true) && (checkedRepeat == false)) {
                            characteristic.setValue(2, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                            mBluetoothLeService!!.writeCharacteristic(characteristic)
                        }
                    }
                    /*if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
                }

                readMachine(ALARM, mGattCharacteristics!![4][0])
                Thread.sleep(100)
                readMachine(SETTIME,mGattCharacteristics!![4][2])

              //  text_alarm.text = hour + " 시  " + minute + " 분"
                k.dismiss()
            }
            //text_alarm.text = alarmTime.toString()

            val thread = Thread(mRunnable)
            thread.start()


        }






/*        button_day.setOnClickListener {
            var hour = timePicker_day.hour
            var minute = timePicker_day.minute
            val msg = hour.toString() + "m : " + minute.toString()

            hour = (hour/10 shl 4) + (hour % 10)
            minute = (minute/10 shl 4) + (minute % 10)


            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()


            val k = builder.setView(dialogView).show()


            mRunnable = Runnable {


                // mHandler.post(mRunnable)


                // Day Time Setting
                if (mGattCharacteristics != null) {
                    val characteristic = mGattCharacteristics!![3][1]
                    val charaProp = characteristic.properties
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
*//*                        mNotifyCharacteristic?.let {
                            mBluetoothLeService!!.setCharacteristicNotification(
                                    it, false)
                            mNotifyCharacteristic = null
                        }*//*

                        // mBluetoothLeService!!.readCharacteristic(characteristic)
                        characteristic.setValue(hour, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                        //text_daytime.text = "ON"
                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }
*//*                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*//*
*//*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*//*
                    Thread.sleep(100)
                }


                // Day Cycle 0 to 1
                if (mGattCharacteristics != null) {
                    val characteristic = mGattCharacteristics!![3][0]
                    val charaProp = characteristic.properties
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
*//*                        mNotifyCharacteristic?.let {
                            mBluetoothLeService!!.setCharacteristicNotification(
                                    it, false)
                            mNotifyCharacteristic = null
                        }*//*

                        // mBluetoothLeService!!.readCharacteristic(characteristic)
                        characteristic.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }
                    *//*if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*//*
*//*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*//*
                }

                readMachine(DAYCYCLE,mGattCharacteristics!![3][0])
                Thread.sleep(100)
                readMachine(DAYTIME,mGattCharacteristics!![3][1])
                k.dismiss()
            }
            val thread = Thread(mRunnable)
            thread.start()
            page_daylight.setBackgroundColor(Color.rgb(255, 165, 0))
            page_mood.setBackgroundColor(Color.WHITE)
        }*/



        button_xday.setOnClickListener {

            mRunnable = Runnable {

            // Day Cycle 0 to 1
            if (mGattCharacteristics != null) {
                val characteristic = mGattCharacteristics!![3][0]
                val charaProp = characteristic.properties
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
/*                    mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                            it, false)
                        mNotifyCharacteristic = null }*/

                    // mBluetoothLeService!!.readCharacteristic(characteristic)
                    characteristic.setValue(0,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                }
/*                if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    mNotifyCharacteristic = characteristic
                    mBluetoothLeService!!.setCharacteristicNotification(
                            characteristic, true)
                }*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
                readMachine(DAYCYCLE,mGattCharacteristics!![3][0])
            }
            // Day Time Setting
            }
            val thread = Thread(mRunnable)
            thread.start()
            page_daylight.setBackgroundColor(Color.WHITE)
           // page_mood.setBackgroundColor(Color.WHITE)
        }


        checkBox_daycycle.setOnCheckedChangeListener { buttonView, isChecked ->


            if (mGattCharacteristics != null) {
                val characteristic = mGattCharacteristics!![3][0]
                val charaProp = characteristic.properties
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                    /*  mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                        it, false)
                    mNotifyCharacteristic = null }
*/

                    // mBluetoothLeService!!.readCharacteristic(characteristic)
                    if(isChecked){
                        characteristic.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }
                    else{characteristic.setValue(254, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                        mBluetoothLeService!!.writeCharacteristic(characteristic)}

                }
                /* if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                mNotifyCharacteristic = characteristic
                mBluetoothLeService!!.setCharacteristicNotification(
                        characteristic, true)
            }*/
                //  mPage_setting!!.visibility = VISIBLE
                //  mPage_light!!.visibility = INVISIBLE


            }
            Thread.sleep(100)
        }

        seekBar_brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                text_brightness.text = "$i"

                if(b) {  //seekbar에 의하여 변경되었을때만 반영

                    mRunnable = Runnable {

                        intensity(i)
                        Thread.sleep(200)
                        intensity2(i)

/*                    if (mGattCharacteristics != null) {
                        val characteristic = mGattCharacteristics!![2][0]
                        val charaProp = characteristic.properties
                        if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
                            *//*  mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                                it, false)
                            mNotifyCharacteristic = null }
*//*

                            // mBluetoothLeService!!.readCharacteristic(characteristic)

                            characteristic.setValue(i, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                            mBluetoothLeService!!.writeCharacteristic(characteristic)
                            Thread.sleep(200)
                        }
                        *//* if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*//*
                        //  mPage_setting!!.visibility = VISIBLE
                        //  mPage_light!!.visibility = INVISIBLE

                        // return
                    }
                    //    mPage_setting!!.visibility = VISIBLE
                    //    mPage_light!!.visibility = INVISIBLE
                    if (mGattCharacteristics2 != null) {
                        val characteristic = mGattCharacteristics2!![2][0]
                        val charaProp = characteristic.properties
                        if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
                            *//*  mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                                it, false)
                            mNotifyCharacteristic = null }
*//*

                            // mBluetoothLeService!!.readCharacteristic(characteristic)

                            characteristic.setValue(i, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                            mBluetoothLeService2!!.writeCharacteristic2(characteristic)

                        }
                        *//* if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*//*
                        //  mPage_setting!!.visibility = VISIBLE
                        //  mPage_light!!.visibility = INVISIBLE


                    }*/
                }
                    val thread = Thread(mRunnable)
                    thread.start()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
               // Toast.makeText(applicationContext,"start tracking",Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
               // Toast.makeText(applicationContext,"stop tracking",Toast.LENGTH_SHORT).show()
            }

        })


        seekBar_temperature.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                text_temperature.text = "$i"+"00 K"

                if(b) {
                  /*  if (mGattCharacteristics != null) {
                        val characteristic = mGattCharacteristics!![2][1]
                        val charaProp = characteristic.properties
                        if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
                            *//*        mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                                it, false)
                            mNotifyCharacteristic = null }*//*

                            // mBluetoothLeService!!.readCharacteristic(characteristic)
                            characteristic.setValue(i, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                            mBluetoothLeService!!.writeCharacteristic(characteristic)
                        }
*//*                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*//*
                        //mPage_setting!!.visibility = VISIBLE
                        //mPage_light!!.visibility = INVISIBLE
                        return
                    }
                    //    mPage_setting!!.visibility = VISIBLE
                    //    mPage_light!!.visibility = INVISIBLE
*/

                    mRunnable = Runnable {

                        writeTemperature(i)
                        Thread.sleep(200)
                        writeTemperature2(i)
                    }
                    val thread = Thread(mRunnable)
                    thread.start()

                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
               // Toast.makeText(applicationContext,"start tracking",Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
               // Toast.makeText(applicationContext,"stop tracking",Toast.LENGTH_SHORT).show()
            }

        })


/*
        seekBar_latitude.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                text_angle.text = "$i"


                if (mGattCharacteristics != null) {
                    val characteristic = mGattCharacteristics!![2][2]
                    val charaProp = characteristic.properties
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.


                        // mBluetoothLeService!!.readCharacteristic(characteristic)
                        characteristic.setValue(i,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }

                  //  mPage_setting!!.visibility = VISIBLE
                  //  mPage_light!!.visibility = INVISIBLE
                    return
                }
                //    mPage_setting!!.visibility = VISIBLE
                //    mPage_light!!.visibility = INVISIBLE


            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
               // Toast.makeText(applicationContext,"start tracking",Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
               // Toast.makeText(applicationContext,"stop tracking",Toast.LENGTH_SHORT).show()
            }

        })
*/


        seekBar_vitaminD.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                text_uvb.text = "$i"
                text_vitaminD.text = "$i"


                if (mGattCharacteristics != null) {
                    val characteristic = mGattCharacteristics!![3][2]
                    val charaProp = characteristic.properties
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.


                        // mBluetoothLeService!!.readCharacteristic(characteristic)
                        characteristic.setValue(i,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }

                    //  mPage_setting!!.visibility = VISIBLE
                    //  mPage_light!!.visibility = INVISIBLE
                    return
                }
                //    mPage_setting!!.visibility = VISIBLE
                //    mPage_light!!.visibility = INVISIBLE


            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
                // Toast.makeText(applicationContext,"start tracking",Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
                // Toast.makeText(applicationContext,"stop tracking",Toast.LENGTH_SHORT).show()
            }

        })

        seekBar_sky.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                text_bg.text = "$i"

                if(b) {  //seekbar에 의하여 변경되었을때만 반영

                    mRunnable = Runnable {


                        // mHandler.post(mRunnable)
                        val thread = Thread(mRunnable)
                        thread.start()
                        if (mGattCharacteristics != null) {

                            for (rowIndex in mGattCharacteristics!!.indices) {
                                val row = mGattCharacteristics!![rowIndex]
                                if (row != null) {
                                    for (columnIndex in row.indices) {
                                        if (BLRU == row[columnIndex].uuid) {
                                            val characteristic = row[columnIndex]
                                            val charaProp = characteristic.properties

                                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                                // If there is an active notification on a characteristic, clear
                                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                                characteristic.setValue(dailyColorCodeUpRed[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                                            }


                                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                                            Thread.sleep(100)
                                        }
                                        if (BLGU == row[columnIndex].uuid) {
                                            val characteristic = row[columnIndex]
                                            val charaProp = characteristic.properties

                                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                                // If there is an active notification on a characteristic, clear
                                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                                characteristic.setValue(dailyColorCodeUpGreen[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                                            }


                                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                                            Thread.sleep(100)
                                        }
                                        if (BLBU == row[columnIndex].uuid) {
                                            val characteristic = row[columnIndex]
                                            val charaProp = characteristic.properties

                                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                                // If there is an active notification on a characteristic, clear
                                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                                characteristic.setValue(dailyColorCodeUpBlue[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                                            }


                                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                                            Thread.sleep(100)
                                        }
                                        if (BLRD == row[columnIndex].uuid) {
                                            val characteristic = row[columnIndex]
                                            val charaProp = characteristic.properties

                                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                                // If there is an active notification on a characteristic, clear
                                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                                characteristic.setValue(dailyColorCodeDownRed[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                                            }


                                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                                            Thread.sleep(100)
                                        }
                                        if (BLGD == row[columnIndex].uuid) {
                                            val characteristic = row[columnIndex]
                                            val charaProp = characteristic.properties

                                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                                // If there is an active notification on a characteristic, clear
                                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                                characteristic.setValue(dailyColorCodeDownGreen[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                                            }


                                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                                            Thread.sleep(100)
                                        }
                                        if (BLBD == row[columnIndex].uuid) {
                                            val characteristic = row[columnIndex]
                                            val charaProp = characteristic.properties

                                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                                // If there is an active notification on a characteristic, clear
                                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                                characteristic.setValue(dailyColorCodeDownBlue[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                                            }


                                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                                            Thread.sleep(100)
                                        }


                                    }

                                    //mHandler.sendEmptyMessage(0)

                                }
                            }

                        }
                        turnOnBackLight()
                    }
                    val thread = Thread(mRunnable)
                    thread.start()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
                // Toast.makeText(applicationContext,"start tracking",Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
                // Toast.makeText(applicationContext,"stop tracking",Toast.LENGTH_SHORT).show()
            }

        })


        button_sun.setOnClickListener {
            //val hour = timePicker_day.hour
           // val minute = timePicker_day.minute
           // val msg = hour.toString() + "m : " + minute.toString()
            val status = button_sun.text


            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()


            val k = builder.setView(dialogView).show()

            mRunnable = Runnable {

                if (status=="시 작"||status=="On") {
                    // Day Cycle 0 to 1
                    if (mGattCharacteristics != null) {
                        val characteristic = mGattCharacteristics!![2][0]
                        val charaProp = characteristic.properties
                        if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.

/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*

*/
/*                            mNotifyCharacteristic?.let {
                                mBluetoothLeService!!.setCharacteristicNotification(
                                        it, false)
                                mNotifyCharacteristic = null
                            }*/


                            // mBluetoothLeService!!.readCharacteristic(characteristic)
                                characteristic.setValue(100, BluetoothGattCharacteristic.FORMAT_UINT8, 0)

                            mBluetoothLeService!!.writeCharacteristic(characteristic)
                        }

/*                        if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                            mNotifyCharacteristic = characteristic
                            mBluetoothLeService!!.setCharacteristicNotification(
                                    characteristic, true)
                        }*//*

*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/

                        Thread.sleep(100)
                    }
                    if (mGattCharacteristics != null) {
                        val characteristic = mGattCharacteristics!![2][1]
                        val charaProp = characteristic.properties
                        if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.

/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*

*/
/*                            mNotifyCharacteristic?.let {
                                mBluetoothLeService!!.setCharacteristicNotification(
                                        it, false)
                                mNotifyCharacteristic = null
                            }*/


                            // mBluetoothLeService!!.readCharacteristic(characteristic)
                                characteristic.setValue(46, BluetoothGattCharacteristic.FORMAT_UINT8, 0)


                            mBluetoothLeService!!.writeCharacteristic(characteristic)
                        }

/*                        if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                            mNotifyCharacteristic = characteristic
                            mBluetoothLeService!!.setCharacteristicNotification(
                                    characteristic, true)
                        }*//*

*/
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/

                    }

                }
               else {
                    // Day Cycle 0 to 1
                    if (mGattCharacteristics != null) {
                        val characteristic = mGattCharacteristics!![2][0]
                        val charaProp = characteristic.properties
                        if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.

/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*

*/
/*                            mNotifyCharacteristic?.let {
                                mBluetoothLeService!!.setCharacteristicNotification(
                                        it, false)
                                mNotifyCharacteristic = null
                            }*/


                            // mBluetoothLeService!!.readCharacteristic(characteristic)

                                characteristic.setValue(0, BluetoothGattCharacteristic.FORMAT_UINT8, 0)



                            mBluetoothLeService!!.writeCharacteristic(characteristic)
                        }

/*                        if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                            mNotifyCharacteristic = characteristic
                            mBluetoothLeService!!.setCharacteristicNotification(
                                    characteristic, true)
                        }*/


/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/

                        Thread.sleep(100)
                    }

                }

                k.dismiss()

                if (status=="시 작") {
                    button_sun.text = "종 료"
                }
                else if (status=="On"){
                    button_sun.text = "Off"
                }
                else if (status=="종 료"){
                    button_sun.text = "시 작"
                }
                else if (status=="Off"){
                    button_sun.text = "On"
                }

            }

            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()






        }


        button_1.setOnClickListener {

            mRunnable = Runnable {

                writeBrightness(100)
                Thread.sleep(100)
                writeTemperature(30)
                Thread.sleep(100)
                goLeft(48)
                Thread.sleep(100)
                writeSamsungcntColor(0)
                Thread.sleep(100)
                turnOnBackLight()

            }

            //mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()


        }
        button_2.setOnClickListener {
            mRunnable = Runnable {
                writeBrightness(100)
                Thread.sleep(100)
                writeTemperature(30)
                Thread.sleep(100)
                goRight(48)
                Thread.sleep(100)
                writeSamsungcntColor(1)
                Thread.sleep(100)
                turnOnBackLight()
            }

            //mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()
        }




       /* button_vd.setOnClickListener {
           // val hour = timePicker_day.hour
           // val minute = timePicker_day.minute
           // val msg = hour.toString() + "m : " + minute.toString()
            val status = button_vd.text

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


            val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
            //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
            animationView.setAnimation("8661-rotating-sun.json")
            animationView.loop(true)
            animationView.playAnimation()


            val k = builder.setView(dialogView).show()

            mRunnable = Runnable {
                // uv 0 to 1
                if (mGattCharacteristics != null) {
                    val characteristic = mGattCharacteristics!![3][2]
                    val charaProp = characteristic.properties
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
*//*                        mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                                it, false)
                            mNotifyCharacteristic = null }*//*

                        // mBluetoothLeService!!.readCharacteristic(characteristic)
                        if (status=="시 작"){
                            characteristic.setValue(1,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                            button_vd.text = "종 료"
                            text_uvb.text = "ON"
                        }
                        else{
                            characteristic.setValue(0,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                            button_vd.text = "시 작"
                            text_uvb.text = "OFF"

                        }

                        mBluetoothLeService!!.writeCharacteristic(characteristic)
                    }
*//*                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*//*
*//*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE *//*
                }

                k.dismiss()


            }

            // mHandler.post(mRunnable)
            val thread = Thread(mRunnable)
            thread.start()






        }*/


        btnColorPicker.setOnClickListener {
            upperlower=false
            this.colorR.progress=Color.red(upperColor)
            this.colorG.progress=Color.green(upperColor)
            this.colorB.progress=Color.blue(upperColor)
            Thread.sleep(100)

            colorSelector.visibility = View.VISIBLE

        }

       /* btnColorSelected.setOnClickListener {
            colorSelector.visibility = View.VISIBLE
        }*/

        btnLowerColorPicker.setOnClickListener {
            upperlower=true
            this.colorR.progress=Color.red(lowerColor)
            this.colorG.progress=Color.green(lowerColor)
            this.colorB.progress=Color.blue(lowerColor)
            Thread.sleep(100)
            colorSelector.visibility = View.VISIBLE
        }

        strColor.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.length == 6){
                    //colorA.progress = 255
                    colorR.progress = Integer.parseInt(s.substring(0..1), 16)
                    colorG.progress = Integer.parseInt(s.substring(2..3), 16)
                    colorB.progress = Integer.parseInt(s.substring(4..5), 16)
                } else if (s.length == 8){
                   // colorA.progress = Integer.parseInt(s.substring(0..1), 16)
                    colorR.progress = Integer.parseInt(s.substring(2..3), 16)
                    colorG.progress = Integer.parseInt(s.substring(4..5), 16)
                    colorB.progress = Integer.parseInt(s.substring(6..7), 16)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })

/*        colorA.max = 255
        colorA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })*/

        colorR.max = 255
        colorR.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        colorG.max = 255
        colorG.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        colorB.max = 255
        colorB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        colorCancelBtn.setOnClickListener {
            colorSelector.visibility = View.GONE
        }

        colorOkBtn.setOnClickListener {
            val color:String = getColorString()
            var drawable = btnColorSelected.background as GradientDrawable
            var drawable2 = btnColorSelected2.background as GradientDrawable
            if(upperlower==false){
            //btnColorSelected.setBackgroundColor(Color.parseColor(color))
                upperColor=Color.parseColor(color)
                drawable.colors=intArrayOf(upperColor, lowerColor)
                drawable2.colors=intArrayOf(upperColor, lowerColor)
            }
            if(upperlower==true) {
                lowerColor=Color.parseColor(color)
                drawable.colors = intArrayOf(upperColor,lowerColor)
                drawable2.colors = intArrayOf(upperColor,lowerColor)
            }
            colorSelector.visibility = View.GONE
            if(upperlower==false) {


                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


                val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
                //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
                animationView.setAnimation("8661-rotating-sun.json")
                animationView.loop(true)
                animationView.playAnimation()


                val k = builder.setView(dialogView).show()

                mRunnable = Runnable {
                    writeUpperColor()
                    k.dismiss()
                    turnOnBackLight()


                }

                // mHandler.post(mRunnable)
                val thread = Thread(mRunnable)
                thread.start()




            }
            else if(upperlower==true) {
                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


                val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
                //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
                animationView.setAnimation("8661-rotating-sun.json")
                animationView.loop(true)
                animationView.playAnimation()


                val k = builder.setView(dialogView).show()

                mRunnable = Runnable {
                    writeLowerColor()
                    k.dismiss()
                    turnOnBackLight()


                }

                // mHandler.post(mRunnable)
                val thread = Thread(mRunnable)
                thread.start()
            }
        }



        button_language.setOnClickListener {

            changeLanguage()
        }


        //samsungcnt()

        //모서리 둥글게
        sunsetpurple.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }
        sunsetred.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }
        greenblueblack.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }
        yellowblack.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }
        seablue.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }
        sunsetorange.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }
        greenblue.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }
        plane.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }
        sunsetorangepurple.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }
        sky2.apply {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            clipToOutline= true
        }


    }

    fun changeLanguage(){
        val lan = button_language.text

        if(lan=="한 글"){
            button_language.text = "English"
            light.text = "sunlight control"
            scenario.text ="sunlight scenario"
            alarm.text = "sunlight alarm"
            sun.text = "bright sunlight"
            vitaminD.text = "Vitamin D synthesis"
            sky.text = "sky control"

            title_brightness2.text = "Brightness : "
            title_temperature2.text = "Color Tem : "
            title_angle2.text = "Angle : "
            title_daytime.text = "Day Cycle : "
            title_uvb.text = "UVB : "
            title_alarm2.text = "Alarm : "
            title_curtime.text = "Cur Time : "

            text_sunshine_control.text = "Sunshine Control"
            text_light_control.text = "Brightness Control"
            text_temperature_control.text = "Color Temperature Control"
            text_angle_control.text = "Angle Adjustment"
            text_angle_control_byhand.text = "Angle Adjustment by Hand"


            //title_brightness.text = "Brightness : "
           // title_temperature.text = "Color Tem : "
           // title_angle.text = "Angle : "

            title_scenario.text = "Scenario Page"
            title_daylight_scenario.text = "Sunshine Daycycle Scenario"
            button_day.text = "On"
            button_xday.text = "Off"

/*            title_mood.text = "Mood Sunlight"
            title_sunset.text = "Sunset Sunlight"
            title_dawn.text = "Dawn Sunlight"*/

            title_alarm.text = "Alarm Page"
            checkBox_repeat.text = "Repeat Every Day"
            radioButton_alarmOn.text = "Sunlight On Alarm"
            radioButton_alarmOff.text = "Sunlight Off Alarm"
            button_alarm.text = "On"
            button_alarmx.text = "Off"

            button_language.text = "English"

            button_sun.text = "On"

            //button_vd.text = "On"

            text_sky_control.text = "Sky Color Control"
            daytime_sky.text = "Skylight Control by Time"

            text_view.text = "Please choose your favorite sunlight"
            text_light_and_sky_control.text = "Please choose the sun of your choice"
            checkBox_daycycle.text = "Start the Circadian"
            sun_setting_title.text = "Sunlight detail control"

        }
        else if(lan=="English"){
            button_language.text = "한 글"
            light.text = "햇빛 조절"
            scenario.text ="햇빛 시나리오"
            alarm.text = "햇빛 알람"
            sun.text = "밝은 햇빛"
            vitaminD.text = "비타민D 합성"
            sky.text = "배경색 조절"

            title_brightness2.text = "밝기 : "
            title_temperature2.text = "색 온도 : "
            title_angle2.text = "각도 : "
            title_daytime.text = "일주기 : "
            title_uvb.text = "UVB : "
            title_alarm2.text = "알람 : "
            title_curtime.text = "현재 시간 : "

            text_sunshine_control.text = "햇빛 조절"
            text_light_control.text = "밝기 조절"
            text_temperature_control.text = "색온도 조절"
            text_angle_control.text = "각도 조절"
            text_angle_control_byhand.text = "각도 수동 조절"

           // title_brightness.text = "밝기 : "
           // title_temperature.text = "색온도 : "
           // title_angle.text = "각도 : "

            title_scenario.text = "시나리오 Page"
            title_daylight_scenario.text = "햇빛 일주기 시나리오"
            button_day.text = "켜기"
            button_xday.text = "끄기"

/*            title_mood.text = "무드 햇빛"
            title_sunset.text = "노을 햇빛"
            title_dawn.text = "새벽 햇빛"*/

            title_alarm.text = "알람 Page"
            checkBox_repeat.text = "매일 반복"
            radioButton_alarmOn.text = "햇빛 켜기 알람"
            radioButton_alarmOff.text = "햇빛 끄기 알람"
            button_alarm.text = "설정"
            button_alarmx.text = "해제"

            button_language.text = "한 글"

            button_sun.text = "켜기"

            //button_vd.text = "켜기"

            text_sky_control.text = "하늘색 선택"
            daytime_sky.text = "시간대 별 하늘색 선택"

            text_view.text = "좋아하는 하늘을 선택해 주세요"
            text_light_and_sky_control.text = "원하는 시간대의 태양을 선택하세요"
            checkBox_daycycle.text = "일주기 CYCLE을 시작 합니다"
            sun_setting_title.text = "햇빛 상세 조절"
        }
    }

    fun getColorString(): String {
/*        var a = Integer.toHexString(((255*colorA.progress)/colorA.max))
        if(upperlower==false){
            UALPHA = ((255*colorA.progress)/colorA.max)
        }
        else if(upperlower==true){
            DALPHA = ((255*colorA.progress)/colorA.max)
        }
        if(a.length==1) a = "0"+a*/


        var r = Integer.toHexString(((255*colorR.progress)/colorR.max))
        if(upperlower==false) {
            URED = ((255 * colorR.progress) / colorR.max)
        }
        else if(upperlower==true){
            DRED = ((255 * colorR.progress) / colorR.max)
        }
        if(r.length==1) r = "0"+r


        var g = Integer.toHexString(((255*colorG.progress)/colorG.max))
        if(upperlower==false) {
            UGREEN = ((255 * colorG.progress) / colorG.max)
        }
        else if(upperlower==true){
            DGREEN = ((255 * colorG.progress) / colorG.max)
        }
            if(g.length==1) g = "0"+g


        var b = Integer.toHexString(((255*colorB.progress)/colorB.max))
        if(upperlower==false) {
            UBLUE = ((255 * colorB.progress) / colorB.max)
        }
        else if(upperlower==true){
            DBLUE = ((255 * colorB.progress) / colorB.max)
        }
            if(b.length==1) b = "0"+b

        //return "#" + a + r + g + b
        return "#" + r + g + b
    }






    fun goLeft(ms:Int){
        if (mGattCharacteristics != null) {

                val characteristic = mGattCharacteristics!![7][3]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
               /*                 mNotifyCharacteristic?.let {
                                    mBluetoothLeService!!.setCharacteristicNotification(
                                            it, false)
                                    mNotifyCharacteristic = null
                                }*/

                                characteristic.setValue(ms, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)

                            }
   /*                         if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                                mNotifyCharacteristic = characteristic
                                mBluetoothLeService!!.setCharacteristicNotification(
                                        characteristic, true)
                            }*/

                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            //Thread.sleep(100)
                        }
    }

    fun goRight(ms:Int){
        if (mGattCharacteristics != null) {

            val characteristic = mGattCharacteristics!![7][4]
            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
/*                                mNotifyCharacteristic?.let {
                                    mBluetoothLeService!!.setCharacteristicNotification(
                                            it, false)
                                    mNotifyCharacteristic = null
                                }*/

                                characteristic.setValue(ms, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }
   /*                         if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                                mNotifyCharacteristic = characteristic
                                mBluetoothLeService!!.setCharacteristicNotification(
                                        characteristic, true)
                            }*/

                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            //Thread.sleep(100)
                        }
                            //mHandler.sendEmptyMessage(0)
                    }








    fun goLeftStraight(){
        if (mGattCharacteristics != null) {

            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {
                        if (CCW == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/

                                characteristic.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }


                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                           // Thread.sleep(100)
                        }

                    }

                    //mHandler.sendEmptyMessage(0)

                }
            }
        }

    }
    fun goRightStraight(){
        if (mGattCharacteristics != null) {

            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {
                        if (CW == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                characteristic.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }


                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                           // Thread.sleep(100)
                        }

                    }

                    //mHandler.sendEmptyMessage(0)

                }
            }
        }

    }
    fun goStop(){
        if (mGattCharacteristics != null) {

            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {
                        if (STOP == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                                mNotifyCharacteristic?.let {
                                    mBluetoothLeService!!.setCharacteristicNotification(
                                            it, false)
                                    mNotifyCharacteristic = null
                                }

                                characteristic.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }
                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                                mNotifyCharacteristic = characteristic
                                mBluetoothLeService!!.setCharacteristicNotification(
                                        characteristic, true)
                            }

                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(100)
                        }

                    }

                    //mHandler.sendEmptyMessage(0)

                }
            }
        }

    }

    fun turnOnBackLight() {
        if (mGattCharacteristics != null) {

            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {
                        if (BLON == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                characteristic.setValue(16, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }


                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(100)
                        }
                    }
                }
            }
        }
    }


    fun writeUpperColor(){
        if (mGattCharacteristics != null) {

            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {
                        if (BLRU == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                characteristic.setValue(URED, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }


                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(100)
                        }
                        if (BLGU == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                characteristic.setValue(UGREEN, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }


                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(100)
                        }
                        if (BLBU == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                characteristic.setValue(UBLUE, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }


                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(100)
                        }
                    }

                    //mHandler.sendEmptyMessage(0)

                }
            }
        }

    }

    fun writeLowerColor(){
        if (mGattCharacteristics != null) {

            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {

                        if (BLRD == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                characteristic.setValue(DRED, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }


                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(100)
                        }
                        if (BLGD == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                characteristic.setValue(DGREEN, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }


                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(100)
                        }
                        if (BLBD == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                characteristic.setValue(DBLUE, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }


                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(100)
                        }





                    }

                    //mHandler.sendEmptyMessage(0)

                }
            }
        }

    }


    fun writeDemoColor(i:Int){
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][0]
            val charaProp = characteristic.properties
                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                    characteristic.setValue(demoColorCodeUpRed[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                                }


                                //mBluetoothLeService!!.readCharacteristic(characteristic)
                                Thread.sleep(150)
                            }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][1]
            val charaProp = characteristic.properties
                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                    characteristic.setValue(demoColorCodeUpGreen[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                                }


                                //mBluetoothLeService!!.readCharacteristic(characteristic)
                                Thread.sleep(150)
                            }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][2]
            val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                    characteristic.setValue(demoColorCodeUpBlue[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                                }


                                //mBluetoothLeService!!.readCharacteristic(characteristic)
                                Thread.sleep(150)
                            }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][3]
            val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                    characteristic.setValue(demoColorCodeDownRed[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                                }


                                //mBluetoothLeService!!.readCharacteristic(characteristic)
                                Thread.sleep(150)
                            }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][4]
            val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                    characteristic.setValue(demoColorCodeDownGreen[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                                }


                                //mBluetoothLeService!!.readCharacteristic(characteristic)
                                Thread.sleep(150)
                            }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][5]
            val charaProp = characteristic.properties

                                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                                    characteristic.setValue(demoColorCodeDownBlue[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                    mBluetoothLeService!!.writeCharacteristic(characteristic)
                                }


                                //mBluetoothLeService!!.readCharacteristic(characteristic)
                                Thread.sleep(150)
                            }
                        //mHandler.sendEmptyMessage(0)

       }

    fun writeSamsungcntColor(i:Int){
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][0]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                characteristic.setValue(scntColorCodeUpRed[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }


            //mBluetoothLeService!!.readCharacteristic(characteristic)
            Thread.sleep(150)
        }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][1]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                characteristic.setValue(scntColorCodeUpGreen[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }


            //mBluetoothLeService!!.readCharacteristic(characteristic)
            Thread.sleep(150)
        }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][2]
            val charaProp = characteristic.properties

            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                characteristic.setValue(scntColorCodeUpBlue[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }


            //mBluetoothLeService!!.readCharacteristic(characteristic)
            Thread.sleep(150)
        }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][3]
            val charaProp = characteristic.properties

            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                characteristic.setValue(scntColorCodeDownRed[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }


            //mBluetoothLeService!!.readCharacteristic(characteristic)
            Thread.sleep(150)
        }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][4]
            val charaProp = characteristic.properties

            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                characteristic.setValue(scntColorCodeDownGreen[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }


            //mBluetoothLeService!!.readCharacteristic(characteristic)
            Thread.sleep(150)
        }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![5][5]
            val charaProp = characteristic.properties

            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/


                characteristic.setValue(scntColorCodeDownBlue[i], BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }


            //mBluetoothLeService!!.readCharacteristic(characteristic)
            Thread.sleep(150)
        }
        //mHandler.sendEmptyMessage(0)

    }

    fun writeCurTime(){
        val format1 = SimpleDateFormat("HH")
        val format2 = SimpleDateFormat("mm")
        val format3 = SimpleDateFormat("ss")

        val tz = TimeZone.getTimeZone("Asia/Seoul")

        val gc = GregorianCalendar(tz)

/*
            var year = gc.get(GregorianCalendar.YEAR).toString()

            var month = gc.get(GregorianCalendar.MONTH).toString()

            var day = gc.get(GregorianCalendar.DATE).toString()

            var hour= gc.get(GregorianCalendar.HOUR).toString()

            var min = gc.get(GregorianCalendar.MINUTE).toString()

            var sec = gc.get(GregorianCalendar.SECOND).toString()
*/



        var hour = format1.format(gc.time)
        var minute = format2.format(gc.time)
        var second = format3.format(gc.time)
/*            if(Integer.parseInt(hour)<9){
                hour="0"+hour
            }
            if(Integer.parseInt(minute)<9){
                minute="0"+minute

            }*/

        var hourInt= Integer.parseInt(hour)
        var minuteInt= Integer.parseInt(minute)
        var secondInt = Integer.parseInt(second)
        hourInt = hourInt
        hourInt = (hourInt/10 shl 4) + (hourInt % 10)
        minuteInt = (minuteInt/10 shl 4) + (minuteInt % 10)
        secondInt = (secondInt/10 shl 4) + (secondInt % 10)

        secondInt = secondInt shl 16
        minuteInt = minuteInt shl 8
        var localTimeInt = hourInt+minuteInt+secondInt

        var localTime = hour + minute + second

        var localTime2 = Integer.parseInt(localTime)


        mRunnable = Runnable {

            writeTime(localTimeInt)


        }
        val thread = Thread(mRunnable)
        thread.start()


    }

    fun writeCurTime2(){
        val format1 = SimpleDateFormat("HH")
        val format2 = SimpleDateFormat("mm")
        val format3 = SimpleDateFormat("ss")

        val tz = TimeZone.getTimeZone("Asia/Seoul")

        val gc = GregorianCalendar(tz)

/*
            var year = gc.get(GregorianCalendar.YEAR).toString()

            var month = gc.get(GregorianCalendar.MONTH).toString()

            var day = gc.get(GregorianCalendar.DATE).toString()

            var hour= gc.get(GregorianCalendar.HOUR).toString()

            var min = gc.get(GregorianCalendar.MINUTE).toString()

            var sec = gc.get(GregorianCalendar.SECOND).toString()
*/



        var hour = format1.format(gc.time)
        var minute = format2.format(gc.time)
        var second = format3.format(gc.time)
/*            if(Integer.parseInt(hour)<9){
                hour="0"+hour
            }
            if(Integer.parseInt(minute)<9){
                minute="0"+minute

            }*/

        var hourInt= Integer.parseInt(hour)
        var minuteInt= Integer.parseInt(minute)
        var secondInt = Integer.parseInt(second)
        hourInt = hourInt
        hourInt = (hourInt/10 shl 4) + (hourInt % 10)
        minuteInt = (minuteInt/10 shl 4) + (minuteInt % 10)
        secondInt = (secondInt/10 shl 4) + (secondInt % 10)

        secondInt = secondInt shl 16
        minuteInt = minuteInt shl 8
        var localTimeInt = hourInt+minuteInt+secondInt

        var localTime = hour + minute + second

        var localTime2 = Integer.parseInt(localTime)


        mRunnable = Runnable {

            writeTime2(localTimeInt)


        }
        val thread = Thread(mRunnable)
        thread.start()


    }


    fun writeTime(t:Int){

        if (mGattCharacteristics != null) {

            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {

                        if (CURRENT == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties
                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/

                                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService!!.setCharacteristicNotification(
                                        mNotifyCharacteristic!!, false)
                                mNotifyCharacteristic = null
                            }
                              mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                            it, false)
                            mNotifyCharacteristic = null }


                                // mBluetoothLeService!!.readCharacteristic(characteristic)
                                characteristic.setValue(t, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                                //mBluetoothLeService2!!.writeCharacteristic2(characteristic)

                            }
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
                        }


                    }
                }
            }

        }

    }

    fun writeTime2(t:Int){

        if (mGattCharacteristics2 != null) {

            for (rowIndex in mGattCharacteristics2!!.indices) {
                val row = mGattCharacteristics2!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {

                        if (CURRENT == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties
                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/

                                if (mNotifyCharacteristic2 != null) {
                                    mBluetoothLeService2!!.setCharacteristicNotification2(
                                            mNotifyCharacteristic2!!, false)
                                    mNotifyCharacteristic2 = null
                                }
                                mNotifyCharacteristic2?.let { mBluetoothLeService2!!.setCharacteristicNotification2(
                                        it, false)
                                    mNotifyCharacteristic2 = null }

                                // mBluetoothLeService!!.readCharacteristic(characteristic)
                                characteristic.setValue(t, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                                //mBluetoothLeService!!.writeCharacteristic(characteristic)
                                mBluetoothLeService2!!.writeCharacteristic2(characteristic)

                            }
/*                mPage_setting!!.visibility = VISIBLE
                mPage_light!!.visibility = INVISIBLE*/
                        }


                    }
                }
            }

        }

    }



    fun writeBrightness(brightness:Int){

        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![2][0]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                /*  mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                    it, false)
                mNotifyCharacteristic = null }
*/

                // mBluetoothLeService!!.readCharacteristic(characteristic)

                characteristic.setValue(brightness, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }
            /* if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            mNotifyCharacteristic = characteristic
            mBluetoothLeService!!.setCharacteristicNotification(
                    characteristic, true)
        }*/
            //  mPage_setting!!.visibility = VISIBLE
            //  mPage_light!!.visibility = INVISIBLE

            return
        }
    }

    fun writeTemperature(temperature:Int){
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![2][1]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                /*        mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                    it, false)
                mNotifyCharacteristic = null }*/

                // mBluetoothLeService!!.readCharacteristic(characteristic)
                characteristic.setValue(temperature, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }
/*                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*/
            //mPage_setting!!.visibility = VISIBLE
            //mPage_light!!.visibility = INVISIBLE
            return
        }
    }

    fun writeTemperature2(temperature:Int){
        if (mGattCharacteristics2 != null) {
            val characteristic = mGattCharacteristics2!![2][1]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                /*        mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                    it, false)
                mNotifyCharacteristic = null }*/

                // mBluetoothLeService!!.readCharacteristic(characteristic)
                characteristic.setValue(temperature, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                mBluetoothLeService2!!.writeCharacteristic2(characteristic)
            }
/*                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                                characteristic, true)
                    }*/
            //mPage_setting!!.visibility = VISIBLE
            //mPage_light!!.visibility = INVISIBLE
            return
        }
    }



    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
        registerReceiver(mGattUpdateReceiver2, makeGattUpdateIntentFilter())
        if (mBluetoothLeService != null) {
            val result = mBluetoothLeService!!.connect(mDeviceAddresses[0])
            Log.d(TAG, "Connect request result=$result")

            Toast.makeText(applicationContext,"connected!!!", Toast.LENGTH_SHORT).show()
        }
        Thread.sleep(100)
        if (mBluetoothLeService != null) {
            val result2 = mBluetoothLeService2!!.connect2(mDeviceAddresses[1])
            Log.d(TAG, "Connect request result=$result2")

            Toast.makeText(applicationContext,"connected!!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGattUpdateReceiver)
        unregisterReceiver(mGattUpdateReceiver2)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        mBluetoothLeService = null
        unbindService(mServiceConnection2)
        mBluetoothLeService2 = null
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        var cMenu: View? = null
        var cMenu2: View? = null
        cMenu =  findViewById(R.id.menu_connect)
        cMenu2 =  findViewById(R.id.menu_disconnect)

        if (colorSelector.visibility == VISIBLE){

            colorSelector.visibility = View.GONE
        }

        else if(this.mPage_demo!!.visibility== VISIBLE){
           // handler.removeCallbacks(demoTask)
           // writeBrightness(0)
            this.mPage_demo!!.visibility = INVISIBLE
            this.mPage_main!!.visibility = VISIBLE
        }
        else if((this.mPage_main!!.visibility== VISIBLE)){


            mBluetoothLeService!!.disconnect()
            mBluetoothLeService2!!.disconnect2()
            val intent = Intent(this, DeviceScanActivity::class.java)
            intent.putExtra(DeviceScanActivity.EXTRAS_ACTIVITY_NAME, "DeviceControlActivity")
            startActivity(intent)
/*            val file = File(this.filesDir, "sunnyside.txt")
            if(file.exists()){
                file.delete()
            }*/
           // mBluetoothLeService!!.disconnect()
           // super.onBackPressed()

        }
        else if((this.mPage_samsungcnt!!.visibility== VISIBLE)){


            mBluetoothLeService!!.disconnect()
            mBluetoothLeService2!!.disconnect2()
            val intent = Intent(this, DeviceScanActivity::class.java)
            intent.putExtra(DeviceScanActivity.EXTRAS_ACTIVITY_NAME, "DeviceControlActivity")
            startActivity(intent)
/*            val file = File(this.filesDir, "sunnyside.txt")
            if(file.exists()){
                file.delete()
            }*/
            // mBluetoothLeService!!.disconnect()
            // super.onBackPressed()

        }


/*        if(cMenu?.visibility == VISIBLE){

            val intent = Intent(this, DeviceScanActivity::class.java)
            startActivity(intent)
        }*/
        else if((this.mPage_main!!.visibility== INVISIBLE)){
            this.mPage_light!!.visibility = View.GONE
            this.mPage_scenario!!.visibility = View.GONE
            this.mPage_alarm!!.visibility = View.GONE
            this.mPage_sun!!.visibility = View.GONE
            this.mPage_vitaminD!!.visibility = View.GONE
            this.mPage_sky!!.visibility = View.GONE
            this.mPage_demo!!.visibility = View.GONE
            this.mPage_view!!.visibility = View.GONE
            this.mPage_main!!.visibility = VISIBLE


            mRunnable = Runnable {

            display_dashboard()
                //writeCurTime()
        }
        val thread = Thread(mRunnable)
        thread.start()

        }




       // this.mPage_setting!!.visibility = INVISIBLE
      //  this.mPage_light!!.visibility = INVISIBLE
       // this.mPage_main!!.visibility = INVISIBLE


    }

    fun test(time:Int){
        if (mGattCharacteristics != null) {

                val characteristic = mGattCharacteristics!![3][1]
                val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                               /* mNotifyCharacteristic?.let {
                                    mBluetoothLeService!!.setCharacteristicNotification(
                                            it, false)
                                    mNotifyCharacteristic = null
                                }*/

                                characteristic.setValue(time, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                text_daytime.text = "OFF"

                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }
                           /* if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                                mNotifyCharacteristic = characteristic
                                mBluetoothLeService!!.setCharacteristicNotification(
                                        characteristic, true)
                            }*/

                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(200)
                        }
            if (mGattCharacteristics != null) {
                val characteristic = mGattCharacteristics!![3][0]
                val charaProp = characteristic.properties

                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                                /*mNotifyCharacteristic?.let {
                                    mBluetoothLeService!!.setCharacteristicNotification(
                                            it, false)
                                    mNotifyCharacteristic = null
                                }*/

                                characteristic.setValue(254, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }
                            /*if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                                mNotifyCharacteristic = characteristic
                                mBluetoothLeService!!.setCharacteristicNotification(
                                        characteristic, true)
                            }*/

                            //mBluetoothLeService!!.readCharacteristic(characteristic)
                            Thread.sleep(100)
                        }



                    //mHandler.sendEmptyMessage(0)



            //writeDemoColor(19)

    }

fun latitude(angle:Int) {
    if (mGattCharacteristics != null) {
        val characteristic = mGattCharacteristics!![2][2]
        val charaProp = characteristic.properties
        if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.


            // mBluetoothLeService!!.readCharacteristic(characteristic)
            characteristic.setValue(angle, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
            mBluetoothLeService!!.writeCharacteristic(characteristic)
        }

        //  mPage_setting!!.visibility = VISIBLE
        //  mPage_light!!.visibility = INVISIBLE
        Thread.sleep(100)
    }
    //    mPage_setting!!.visibility = VISIBLE
    //    mPage_light!!.visibility = INVISIBLE

}


    fun intensity(b:Int) {
        if (mGattCharacteristics != null) {

            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {

                        if (INTENSITY == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties
                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {


                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.

                                if (mNotifyCharacteristic != null) {
                                    mBluetoothLeService!!.setCharacteristicNotification(
                                            mNotifyCharacteristic!!, false)
                                    mNotifyCharacteristic = null
                                }
                                mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                                        it, false)
                                    mNotifyCharacteristic = null }                                // mBluetoothLeService!!.readCharacteristic(characteristic)
                                characteristic.setValue(b, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                mBluetoothLeService!!.writeCharacteristic(characteristic)
                            }

                            //  mPage_setting!!.visibility = VISIBLE
                            //  mPage_light!!.visibility = INVISIBLE


                        }
                        //    mPage_setting!!.visibility = VISIBLE
                        //    mPage_light!!.visibility = INVISIBLE

                    }
                }
            }
        }
    }

    fun intensity2(b:Int) {
        if (mGattCharacteristics2 != null) {
            for (rowIndex in mGattCharacteristics2!!.indices) {
                val row = mGattCharacteristics2!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {

                        if (INTENSITY == row[columnIndex].uuid) {
                            val characteristic = row[columnIndex]
                            val charaProp = characteristic.properties
                            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.

                                if (mNotifyCharacteristic2 != null) {
                                    mBluetoothLeService2!!.setCharacteristicNotification2(
                                            mNotifyCharacteristic2!!, false)
                                    mNotifyCharacteristic2 = null
                                }
                                mNotifyCharacteristic2?.let { mBluetoothLeService2!!.setCharacteristicNotification2(
                                        it, false)
                                    mNotifyCharacteristic2 = null }                                // mBluetoothLeService!!.readCharacteristic(characteristic)
                                characteristic.setValue(b, BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                                //mBluetoothLeService2!!.writeCharacteristic2(characteristic)
                                mBluetoothLeService2!!.writeCharacteristic2(characteristic)
                            }



                            //  mPage_setting!!.visibility = VISIBLE
                            //  mPage_light!!.visibility = INVISIBLE
                        }
                        //    mPage_setting!!.visibility = VISIBLE
                        //    mPage_light!!.visibility = INVISIBLE

                    }
                }
            }
        }
    }

    fun display_dashboard() {
        if (mGattCharacteristics != null) {

            for (rowIndex in mGattCharacteristics!!.indices) {
                val row = mGattCharacteristics!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {

                        readMachine(INTENSITY,row[columnIndex])
                        readMachine(TEMPERATURE,row[columnIndex])
                        readMachine(LATITUDE,row[columnIndex])
                        readMachine(DAYCYCLE,row[columnIndex])
                        readMachine(DAYTIME,row[columnIndex])
                        readMachine(UV,row[columnIndex])
                        readMachine(ALARM,row[columnIndex])
                        readMachine(SETTIME,row[columnIndex])
                        readMachine(CURRENT,row[columnIndex])
                        if (BLRU == row[columnIndex].uuid) {
                            readMachine(BLRU, row[columnIndex])
                        }
                        readMachine(BLGU,row[columnIndex])
                        if (BLBU == row[columnIndex].uuid) {
                            readMachine(BLBU, row[columnIndex])
                        }
                        readMachine(BLRD,row[columnIndex])
                        readMachine(BLGD,row[columnIndex])
                        readMachine(BLBD,row[columnIndex])
                    }
                }
            }
        }
    }

    fun display_dashboard2() {
        if (mGattCharacteristics2 != null) {

            for (rowIndex in mGattCharacteristics2!!.indices) {
                val row = mGattCharacteristics2!![rowIndex]
                if (row != null) {
                    for (columnIndex in row.indices) {

                        readMachine2(INTENSITY,row[columnIndex])
                        readMachine2(TEMPERATURE,row[columnIndex])
                        readMachine2(LATITUDE,row[columnIndex])
                        readMachine2(DAYCYCLE,row[columnIndex])
                        readMachine2(DAYTIME,row[columnIndex])
                        readMachine2(UV,row[columnIndex])
                        readMachine2(ALARM,row[columnIndex])
                        readMachine2(SETTIME,row[columnIndex])
                        readMachine2(CURRENT,row[columnIndex])
                        if (BLRU == row[columnIndex].uuid) {
                            readMachine2(BLRU, row[columnIndex])
                        }
                        readMachine2(BLGU,row[columnIndex])
                        if (BLBU == row[columnIndex].uuid) {
                            readMachine2(BLBU, row[columnIndex])
                        }
                        readMachine2(BLRD,row[columnIndex])
                        readMachine2(BLGD,row[columnIndex])
                        readMachine2(BLBD,row[columnIndex])
                    }
                }
            }
        }
    }

    fun readMachine(uuid1: UUID, characteristic1: BluetoothGattCharacteristic){
        if (uuid1 == characteristic1.uuid) {
            val characteristic = characteristic1
            val charaProp = characteristic.properties

            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic!!, false)
                    mNotifyCharacteristic = null
                }
                mNotifyCharacteristic?.let {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            it, false)
                    mNotifyCharacteristic = null
                }*/
                mBluetoothLeService!!.readCharacteristic(characteristic)
                Thread.sleep(100)
            }
        }
    }
    fun readMachine2(uuid1: UUID, characteristic1: BluetoothGattCharacteristic){
        if (uuid1 == characteristic1.uuid) {
            val characteristic = characteristic1
            val charaProp = characteristic.properties

            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic!!, false)
                    mNotifyCharacteristic = null
                }
                mNotifyCharacteristic?.let {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            it, false)
                    mNotifyCharacteristic = null
                }*/
                mBluetoothLeService2!!.readCharacteristic2(characteristic)
                Thread.sleep(100)
            }
        }
    }

    fun testerVersion(){

        if(testV==0){
             testV = 1
            light.isEnabled = false
            scenario.isEnabled =false
            alarm.isEnabled = false
            sun.isEnabled = false
            vitaminD.isEnabled = false
            sky.isEnabled = false
            testVersion.visibility=VISIBLE
        }

    }

    fun testerVersionOn(){
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![2][0]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                /*if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
/*                 mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                          it, false)
                      mNotifyCharacteristic = null }*/


                // mBluetoothLeService!!.readCharacteristic(characteristic)

                characteristic.setValue(100,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }
/*             if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                 mNotifyCharacteristic = characteristic
                 mBluetoothLeService!!.setCharacteristicNotification(
                         characteristic, true)
             }*/
            //  mPage_setting!!.visibility = VISIBLE
            //  mPage_light!!.visibility = INVISIBLE
                Thread.sleep(100)

        }
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![2][1]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
/*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
                /*  mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                          it, false)
                      mNotifyCharacteristic = null }
*/

                // mBluetoothLeService!!.readCharacteristic(characteristic)

                characteristic.setValue(46,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }
            /* if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                 mNotifyCharacteristic = characteristic
                 mBluetoothLeService!!.setCharacteristicNotification(
                         characteristic, true)
             }*/
            //  mPage_setting!!.visibility = VISIBLE
            //  mPage_light!!.visibility = INVISIBLE
            Thread.sleep(100)
            return
        }
    }

    fun testerVersionOff(){
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![2][0]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                /*if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*/
/*                mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                        it, false)
                    mNotifyCharacteristic = null }*/


                // mBluetoothLeService!!.readCharacteristic(characteristic)

                characteristic.setValue(0,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }
/*            if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                mNotifyCharacteristic = characteristic
                mBluetoothLeService!!.setCharacteristicNotification(
                        characteristic, true)
            }*/
            //  mPage_setting!!.visibility = VISIBLE
            //  mPage_light!!.visibility = INVISIBLE
            Thread.sleep(100)
            return
        }
        /*if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![2][1]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
*//*                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic, false)
                    mNotifyCharacteristic = null
                }*//*
                *//*  mNotifyCharacteristic?.let { mBluetoothLeService!!.setCharacteristicNotification(
                          it, false)
                      mNotifyCharacteristic = null }
*//*

                // mBluetoothLeService!!.readCharacteristic(characteristic)

                characteristic.setValue(46,BluetoothGattCharacteristic.FORMAT_UINT8,0)
                mBluetoothLeService!!.writeCharacteristic(characteristic)
            }
            *//* if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                 mNotifyCharacteristic = characteristic
                 mBluetoothLeService!!.setCharacteristicNotification(
                         characteristic, true)
             }*//*
            //  mPage_setting!!.visibility = VISIBLE
            //  mPage_light!!.visibility = INVISIBLE
            Thread.sleep(100)
            return
        }*/
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.gatt_services, menu)
        if (mConnected!!) {
            menu.findItem(R.id.menu_connect).isVisible = false
            menu.findItem(R.id.menu_disconnect).isVisible = true
        } else {
            menu.findItem(R.id.menu_connect).isVisible = true
            menu.findItem(R.id.menu_disconnect).isVisible = false

            //Toast.makeText(applicationContext,"disconnected!!!", Toast.LENGTH_SHORT).show()

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_connect -> {
                   /* val intent = Intent(this, DeviceScanActivity::class.java)
                    startActivity(intent)
*/
                mBluetoothLeService!!.connect(mDeviceAddresses[0])
                Thread.sleep(100)
                //mBluetoothLeService!!.connect2(mDeviceAddresses[1])
                mBluetoothLeService2!!.connect2(mDeviceAddresses[1])

                return true
            }
            R.id.menu_disconnect -> {
                mBluetoothLeService!!.disconnect()
                //mBluetoothLeService!!.disconnect2()
                val intent = Intent(this, DeviceScanActivity::class.java)
                startActivity(intent)
                mBluetoothLeService2!!.disconnect2()

                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateConnectionState(resourceId: Int) {
        runOnUiThread { mConnectionState!!.setText(resourceId) }

    }

    private fun displayData(data: String?) {
        if (data != null) {
            mDataField!!.text = data
            //Toast.makeText(applicationContext,data, Toast.LENGTH_SHORT).show()

        }
    }

    private fun setUpAnimation(animationView : LottieAnimationView) {
        // 재생할 애니메이션 넣어준다.
        animationView.setAnimation("8661-rotating-sun.json");
        // 반복횟수를 무한히 주고 싶을 땐 LottieDrawable.INFINITE or 원하는 횟수
        animationView.setRepeatCount(LottieDrawable.INFINITE);
        // 시작
        animationView.playAnimation();
    }

    public fun demo(){



    }


    public fun samsungcnt(){
        this.mPage_light!!.visibility = View.GONE
        this.mPage_scenario!!.visibility = View.GONE
        this.mPage_alarm!!.visibility = View.GONE
        this.mPage_sun!!.visibility = View.GONE
        this.mPage_vitaminD!!.visibility = View.GONE
        this.mPage_sky!!.visibility = View.GONE
        this.mPage_demo!!.visibility = View.GONE
        this.mPage_view!!.visibility = View.GONE
        this.mPage_main!!.visibility = View.GONE
        this.mPage_samsungcnt!!.visibility = VISIBLE


    }

    public fun progressON(){




    }

    private fun find2DIndex(array: Array<Array<Any>>?, search: Any?): Point? {

        if (search == null || array == null) return null

        for (rowIndex in array.indices) {
            val row = array[rowIndex]
            if (row != null) {
                for (columnIndex in row.indices) {
                    if (search == row[columnIndex]) {
                        return Point(rowIndex, columnIndex)
                    }
                }
            }
        }
        return null // value not found in array
    }


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return
        var uuid: String? = null
        val unknownServiceString = resources.getString(R.string.unknown_service)
        val unknownCharaString = resources.getString(R.string.unknown_characteristic)
        // val gattServiceData = ArrayList<HashMap<String, String>>()
        // val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String>>>()
        mGattCharacteristics = ArrayList()

        // Loops through available GATT Services.
        for (gattService in gattServices) {
            val currentServiceData = HashMap<String, String>()
            uuid = gattService.uuid.toString()
            currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownServiceString)
            currentServiceData[LIST_UUID] = uuid
            gattServiceData?.add(currentServiceData)

             val gattCharacteristicGroupData = ArrayList<HashMap<String, String>>()
            val gattCharacteristics = gattService.characteristics
            val charas = ArrayList<BluetoothGattCharacteristic>()

            // Loops through available Characteristics.
            for (gattCharacteristic in gattCharacteristics) {
                charas.add(gattCharacteristic)
                val currentCharaData = HashMap<String, String>()
                uuid = gattCharacteristic.uuid.toString()
                currentCharaData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownCharaString)
                currentCharaData[LIST_UUID] = uuid
                gattCharacteristicGroupData.add(currentCharaData)
            }
            mGattCharacteristics!!.add(charas)
            gattCharacteristicData?.add(gattCharacteristicGroupData)
        }

        val gattServiceAdapter = SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                arrayOf(LIST_NAME, LIST_UUID),
                intArrayOf(android.R.id.text1, android.R.id.text2),
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                arrayOf(LIST_NAME, LIST_UUID),
                intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        mGattServicesList!!.setAdapter(gattServiceAdapter)


        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


        val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
        //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
        animationView.setAnimation("8661-rotating-sun.json")
        animationView.loop(true)
        animationView.playAnimation()


        val k = builder.setView(dialogView).show()


        mRunnable = Runnable {

            writeCurTime()
            Thread.sleep(200)
            display_dashboard()
            Thread.sleep(200)


            k.dismiss()

        }

        val thread = Thread(mRunnable)
        thread.start()
        mPage_main!!.visibility = VISIBLE
        //samsungcnt()
    }

    private fun displayGattServices2(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return
        var uuid: String? = null
        val unknownServiceString = resources.getString(R.string.unknown_service)
        val unknownCharaString = resources.getString(R.string.unknown_characteristic)
        // val gattServiceData = ArrayList<HashMap<String, String>>()
        // val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String>>>()
        mGattCharacteristics2 = ArrayList()

        // Loops through available GATT Services.
        for (gattService in gattServices) {
            val currentServiceData = HashMap<String, String>()
            uuid = gattService.uuid.toString()
            currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownServiceString)
            currentServiceData[LIST_UUID] = uuid
            gattServiceData2?.add(currentServiceData)

            val gattCharacteristicGroupData = ArrayList<HashMap<String, String>>()
            val gattCharacteristics = gattService.characteristics
            val charas = ArrayList<BluetoothGattCharacteristic>()

            // Loops through available Characteristics.
            for (gattCharacteristic in gattCharacteristics) {
                charas.add(gattCharacteristic)
                val currentCharaData = HashMap<String, String>()
                uuid = gattCharacteristic.uuid.toString()
                currentCharaData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownCharaString)
                currentCharaData[LIST_UUID] = uuid
                gattCharacteristicGroupData.add(currentCharaData)
            }
            mGattCharacteristics2!!.add(charas)
            gattCharacteristicData2?.add(gattCharacteristicGroupData)
        }

        val gattServiceAdapter = SimpleExpandableListAdapter(
                this,
                gattServiceData2,
                android.R.layout.simple_expandable_list_item_2,
                arrayOf(LIST_NAME, LIST_UUID),
                intArrayOf(android.R.id.text1, android.R.id.text2),
                gattCharacteristicData2,
                android.R.layout.simple_expandable_list_item_2,
                arrayOf(LIST_NAME, LIST_UUID),
                intArrayOf(android.R.id.text1, android.R.id.text2)
        )
       // mGattServicesList2!!.setAdapter(gattServiceAdapter)
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)


        val animationView = dialogView.findViewById<LottieAnimationView>(R.id.lottieAnimView)
        //val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimView)
        animationView.setAnimation("8661-rotating-sun.json")
        animationView.loop(true)
        animationView.playAnimation()


        val k = builder.setView(dialogView).show()

        mRunnable = Runnable {
            //writeCurTime() 여기에 write time 2 를넣자 순서대
            writeCurTime2()
            //display_dashboard()
            display_dashboard2()
            k.dismiss()
        }

        val thread = Thread(mRunnable)
        thread.start()
        mPage_main!!.visibility = VISIBLE
        //samsungcnt()
    }

    companion object {
        var mConnected: Boolean = false
        private val TAG = DeviceControlActivity::class.java!!.getSimpleName()

        //val EXTRAS_DEVICE_NAME = "DEVICE_NAME"
        //val EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"
        // 1개만 선택할 때 쓰던 코드

        val DEVICE_NAMES : Array<String> = arrayOf("0","1","2","3","4","5","6","7","8","9")
         val DEVICE_ADDRESSES : Array<String> = arrayOf("0","1","2","3","4","5","6","7","8","9")

        val LIGHT = UUID.fromString(SampleGattAttributes.LIGHT)
        val INTENSITY = UUID.fromString(SampleGattAttributes.INTENSITY)
        val TEMPERATURE = UUID.fromString(SampleGattAttributes.TEMPERATURE)
        val LATITUDE = UUID.fromString(SampleGattAttributes.LATITUDE)
        val LONGITUDE = UUID.fromString(SampleGattAttributes.LONGITUDE)
        val DAYCYCLE = UUID.fromString(SampleGattAttributes.DAYCYCLE)
        val DAYTIME = UUID.fromString(SampleGattAttributes.DAYTIME)
        val UV = UUID.fromString(SampleGattAttributes.UV)
        val ALARM = UUID.fromString(SampleGattAttributes.ALARM)
        val CURRENT = UUID.fromString(SampleGattAttributes.CURRENT)
        val SETTIME = UUID.fromString(SampleGattAttributes.SETTIME)
        val BLRU = UUID.fromString(SampleGattAttributes.BLRU)
        val BLGU = UUID.fromString(SampleGattAttributes.BLGU)
        val BLBU = UUID.fromString(SampleGattAttributes.BLBU)
        val BLRD = UUID.fromString(SampleGattAttributes.BLRD)
        val BLGD = UUID.fromString(SampleGattAttributes.BLGD)
        val BLBD = UUID.fromString(SampleGattAttributes.BLBD)
        val BLON = UUID.fromString(SampleGattAttributes.BLON)
        val CCW = UUID.fromString(SampleGattAttributes.CCW)
        val CW = UUID.fromString(SampleGattAttributes.CW)
        val STOP = UUID.fromString(SampleGattAttributes.STOP)
        val CCWD = UUID.fromString(SampleGattAttributes.CCWD)
        val CWD = UUID.fromString(SampleGattAttributes.CWD)

        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITTEN)
            return intentFilter
        }
    }
}
