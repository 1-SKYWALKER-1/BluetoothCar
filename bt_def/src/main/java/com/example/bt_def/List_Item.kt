package com.example.bt_def

import android.bluetooth.BluetoothDevice

data class List_Item(
    val device : BluetoothDevice,
    val isChecked: Boolean
) {

}
