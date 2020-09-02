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

import java.util.HashMap

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
object SampleGattAttributes {
    private val attributes = HashMap<String, String>()
    var HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb"
    var CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    var SERVICE_CHANGED = "00002a05-0000-1000-8000-00805f9b34fb"
    var ALERT_LIGHT = "0000fe55-cc7a-482a-984a-7f2ed5b3e58e"


    var LIGHT = "0000fe50-cc7a-482a-984a-7f2ed5b3e58e"
    var INTENSITY = "0000fe51-cc7a-482a-984a-7f2ed5b3e58e"
    var TEMPERATURE = "0000fe52-cc7a-482a-984a-7f2ed5b3e58e"
    var LATITUDE = "0000fe53-cc7a-482a-984a-7f2ed5b3e58e"
    var LONGITUDE = "0000fe54-cc7a-482a-984a-7f2ed5b3e58e"

    var DAYCYCLE = "0000fe61-cc7a-482a-984a-7f2ed5b3e58e"
    var DAYTIME = "0000fe62-cc7a-482a-984a-7f2ed5b3e58e"
    var UV = "0000fe63-cc7a-482a-984a-7f2ed5b3e58e"

    var ALARM = "0000fe71-cc7a-482a-984a-7f2ed5b3e58e"
    var CURRENT = "0000fe72-cc7a-482a-984a-7f2ed5b3e58e"
    var SETTIME = "0000fe73-cc7a-482a-984a-7f2ed5b3e58e"
    var BLRU = "0000fe81-cc7a-482a-984a-7f2ed5b3e58e"
    var BLGU = "0000fe82-cc7a-482a-984a-7f2ed5b3e58e"
    var BLBU = "0000fe83-cc7a-482a-984a-7f2ed5b3e58e"
    var BLRD = "0000fe84-cc7a-482a-984a-7f2ed5b3e58e"
    var BLGD = "0000fe85-cc7a-482a-984a-7f2ed5b3e58e"
    var BLBD = "0000fe86-cc7a-482a-984a-7f2ed5b3e58e"
    var BLON = "0000fe87-cc7a-482a-984a-7f2ed5b3e58e"
    var CCW = "0000fe01-cc7a-482a-984a-7f2ed5b3e58e"
    var CW = "0000fe02-cc7a-482a-984a-7f2ed5b3e58e"
    var STOP = "0000fe03-cc7a-482a-984a-7f2ed5b3e58e"
    var CCWD = "0000fe04-cc7a-482a-984a-7f2ed5b3e58e"
    var CWD = "0000fe05-cc7a-482a-984a-7f2ed5b3e58e"

    init {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service")
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service")
        attributes.put("00001801-0000-1000-8000-00805f9b34fb","Generic Attribute")
        attributes.put("00001800-0000-1000-8000-00805f9b34fb","Generic Access")
        attributes.put("00001812-0000-1000-8000-00805f9b34fb","Human Interface Device")
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement")
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String")
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed")
        attributes.put("00002902-0000-1000-8000-00805f9b34fb", "Client Characteristic Config")
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name")
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance")
        attributes.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Preferred")
        attributes.put("00002a4a-0000-1000-8000-00805f9b34fb", "HID Information");
        attributes.put("00002a4b-0000-1000-8000-00805f9b34fb", "Report Map");
        attributes.put("00002a4c-0000-1000-8000-00805f9b34fb", "HID Control Point");
        attributes.put("00002a4d-0000-1000-8000-00805f9b34fb", "Report");
        attributes.put("0000fe55-cc7a-482a-984a-7f2ed5b3e58e", "Alert_Light");
    }

    fun lookup(uuid: String, defaultName: String): String {
        val name = attributes.get(uuid)
        return if (name == null) defaultName else name
    }
}
