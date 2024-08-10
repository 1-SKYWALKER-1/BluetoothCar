package com.example.bt_def.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.UUID

class ConnectThread(device: BluetoothDevice, val listener: BluetoothController.Listener) :
    Thread() {
    private val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    private var mSocket: BluetoothSocket? = null

    init {
        try {
            mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        } catch (e: IOException) {

        } catch (se: SecurityException) {

        }
    }

    override fun run() {
        try {
            mSocket?.connect()
            listener.onReceive(BluetoothController.BLUETOOTH_CONNECTED)
            readMassage()
        } catch (e: IOException) {
            listener.onReceive(BluetoothController.BLUETOOTH_NO_CONNECTED)
        } catch (se: SecurityException) {

        }
    }
    fun SendMessage(message: String){
        try{
            mSocket?.outputStream?.write(message.toByteArray())
        }catch(e: IOException){

        }
    }
    private fun readMassage() {
        val buffer = ByteArray(256)
        while (true) {
            try {
                val length_buffer = mSocket?.inputStream?.read(buffer)
                val message = String(buffer, 0, length_buffer ?: 0)
                listener.onReceive(message)
            } catch (e: IOException) {
                break
            }
        }

    }

    fun closeConnection() {
        try {
            mSocket?.close()
        } catch (e: IOException) {

        }
    }
}