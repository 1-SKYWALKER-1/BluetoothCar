package com.example.bluetoothcar

import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.bt_def.bluetooth.BluetoothController

class App : Application() {
    val bluetoothManager by lazy {
        getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    val bluetoothController by lazy {
        BluetoothController(bluetoothManager.adapter)
    }
}