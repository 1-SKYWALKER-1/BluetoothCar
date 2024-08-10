package com.example.bt_def

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.UUID

class ConnectThread(private val device: BluetoothDevice) : Thread() {
    val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    var mySocket: BluetoothSocket? = null

    init {
        try {
            mySocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        }catch (i: IOException){

        }
    }

    override fun run() {
        try {
            mySocket?.connect()
            Log.d("MyLog","Connected")
        }catch (i: IOException){
            Log.d("MyLog","Can`t connect to device")
            closeConnection()
        }
    }
     fun closeConnection(){
         try {
             mySocket?.close()
         }catch (i: IOException){

         }
     }
}