package com.example.bt_def

import android.bluetooth.BluetoothAdapter

class BluetoothConnect(private val adapter: BluetoothAdapter) {
    lateinit var connectionThread:ConnectThread
    fun connect(mac: String) {
        if (adapter.isEnabled && mac.isNotEmpty()) {
            val device = adapter.getRemoteDevice(mac)
            device.let {
                connectionThread = ConnectThread(it)
                connectionThread.start()
            }
        }

    }
}