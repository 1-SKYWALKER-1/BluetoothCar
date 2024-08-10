package com.example.bt_def

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bt_def.databinding.BluetoothLayoutBinding
import com.google.android.material.snackbar.Snackbar

class DeviceListFragment : Fragment(), ItemAdapter.Listener {
    private var preferences : SharedPreferences? = null
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var DiscoveryAdapter: ItemAdapter
    private var bAdapter: BluetoothAdapter? = null
    private lateinit var binding: BluetoothLayoutBinding
    private lateinit var btLauncher: ActivityResultLauncher<Intent>
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferences = activity?.getSharedPreferences(BluetoothConstans.PREFERENCES, Context.MODE_PRIVATE)
        binding = BluetoothLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.BluetoothConnect.setOnClickListener(){
            btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
        binding.devicesList.setOnClickListener(){
            try{
                if(bAdapter?.isEnabled == true){
                    bAdapter?.startDiscovery()
                    binding.devicesList.visibility = View.GONE
                    binding.pbSearch.visibility = View.VISIBLE
                }
            }catch(e: SecurityException){

            }
        }
        intentFilters()
        checkPermissions()
        initRcViews()
        registerBtLauncher()
        initBtAdapter()
        BluetoothState()
    }
    private fun initRcViews() = with(binding){
        RcView.layoutManager = LinearLayoutManager(requireContext())
        Rc2View.layoutManager = LinearLayoutManager(requireContext())
        itemAdapter = ItemAdapter(this@DeviceListFragment,false)
        DiscoveryAdapter = ItemAdapter(this@DeviceListFragment, true)
        RcView.adapter = itemAdapter
        Rc2View.adapter = DiscoveryAdapter

    }

private fun getPairedDevices(){
    try{
        val list = ArrayList <List_Item>()
        val DeviceList = bAdapter?.bondedDevices as Set<BluetoothDevice>
        DeviceList.forEach{
            list.add(List_Item(it,preferences?.getString(BluetoothConstans.MAC, " ")==it.address))
        }
        itemAdapter.submitList(list)
    }catch (e: SecurityException){

    }

}


    private fun initBtAdapter(){
        val bManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = bManager.adapter
    }
    private fun BluetoothState(){
        if(bAdapter?.isEnabled == true){
            binding.BluetoothConnect
            binding.devicesList
            getPairedDevices()
        }
        else {
            ChangeColor(binding.BluetoothConnect,Color.RED)
            ChangeColor(binding.devicesList,Color.RED)
        }

    }
    private fun registerBtLauncher(){
        btLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                ChangeColor(binding.BluetoothConnect,Color.GREEN)
                getPairedDevices()
                Snackbar.make(binding.root, "<Bluetooth On!>",Snackbar.LENGTH_LONG).show()
            }else {
                Snackbar.make(binding.root, "<Bluetooth Off!>",Snackbar.LENGTH_LONG).show()
            }
        }

    }
    private fun checkPermissions(){
        if(!checkBtPermissions()){
            registerPermissionListener()
            launchBtPermissions()
        }
    }
    private fun launchBtPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            pLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            pLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }


    private fun registerPermissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){

        }
    }

    private fun SaveMac(mac : String){
        val editor = preferences?.edit()
        editor?.putString(BluetoothConstans.MAC, mac)
        editor?.apply()
    }
    override fun onClick(item: List_Item) {
        SaveMac(item.device.address)
    }
    private val bReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            if(intent?.action == BluetoothDevice.ACTION_FOUND){
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val list = mutableListOf<List_Item>()
                list.addAll(DiscoveryAdapter.currentList)
                if(device != null) list.add(List_Item(device, false))
                DiscoveryAdapter.submitList(list.toList())
                try{
                    Log.d("MyLog", "Device: ${device?.name}")
                } catch(e: SecurityException){

                }
            } else if(intent?.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                getPairedDevices()
            } else if(intent?.action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED){
                binding.devicesList.visibility = View.VISIBLE
                binding.pbSearch.visibility = View.GONE
            }
        }

    }
    private fun intentFilters(){
        val f1 = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val f2 = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        val f3 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        activity?.registerReceiver(bReceiver, f1)
        activity?.registerReceiver(bReceiver, f2)
        activity?.registerReceiver(bReceiver, f3)
    }
}