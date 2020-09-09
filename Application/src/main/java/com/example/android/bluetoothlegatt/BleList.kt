package com.example.android.bluetoothlegatt

import android.os.Parcel
import android.os.Parcelable


class BleList (var ble1name: String, var ble1address : String, var ble2name: String, var ble2address : String, var ble3name: String, var ble3address : String
, var ble4name: String, var ble4address : String, var ble5name: String, var ble5address : String) :  Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ble1name)
        parcel.writeString(ble1address)
        parcel.writeString(ble2name)
        parcel.writeString(ble2address)
        parcel.writeString(ble3name)
        parcel.writeString(ble3address)
        parcel.writeString(ble4name)
        parcel.writeString(ble4address)
        parcel.writeString(ble5name)
        parcel.writeString(ble5address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BleList> {
        override fun createFromParcel(parcel: Parcel): BleList {
            return BleList(parcel)
        }

        override fun newArray(size: Int): Array<BleList?> {
            return arrayOfNulls(size)
        }
    }
}
