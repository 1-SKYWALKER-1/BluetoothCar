import android.bluetooth.BluetoothAdapter

class BtConnection(val adapter: BluetoothAdapter) {
    fun connect(mac: String){
        if(adapter.isEnabled)
        val device = adapter.getRemoteDevice(mac)
        device.let {

        }
    }
}