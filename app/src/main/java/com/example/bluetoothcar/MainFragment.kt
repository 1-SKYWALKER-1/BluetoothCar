package com.example.bluetoothcar

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bluetoothcar.databinding.FragmentMainBinding
import com.example.bt_def.BluetoothConstans
import com.example.bt_def.bluetooth.BluetoothController

//class MainFragment : Fragment(),BluetoothController.Listener {
//
//    private lateinit var binding: FragmentMainBinding
//    private lateinit var bluetoothController: BluetoothController
//    private lateinit var btAdapter : BluetoothAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        binding = FragmentMainBinding.inflate(inflater, container, false)
//        return binding.root
//
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initBtAdapter()
//        val pref = activity?.getSharedPreferences(BluetoothConstans.PREFERENCES, Context.MODE_PRIVATE)
//        val mac = pref?.getString(BluetoothConstans.MAC, "")
//        bluetoothController = BluetoothController(btAdapter)
//        binding.bList.setOnClickListener {
//            findNavController().navigate(R.id.action_MainFragment_to_deviceListFragment)
//        }
//        binding.connectButton.setOnClickListener{
//
//            bluetoothController.connect(mac ?: "",this)
//        }
//    }
//    private fun initBtAdapter(){
//        val bManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        btAdapter = bManager.adapter
//    }
//
//    override fun onReceive(message: String) {
//        activity?.runOnUiThread {
//            when(message){
//                BluetoothController.BLUETOOTH_CONNECTED -> {
//                    Log.d("MyLog","Send Message: ${message}")
//                    binding.connectButton.backgroundTintList = AppCompatResources
//                        .getColorStateList(requireContext(), R.color.red)
//                    binding.connectButton.text = "Disconnect"
//                }
//                BluetoothController.BLUETOOTH_NO_CONNECTED -> {
//                    Log.d("MyLog","Send Message: ${message}")
//                    binding.connectButton.backgroundTintList = AppCompatResources
//                        .getColorStateList(requireContext(), R.color.green)
//                    binding.connectButton.text = "Connect"
//                }
//                else ->{
//
//                }
//            }
//        }
//    }
//}

class MainFragment : Fragment(), BluetoothController.Listener {
    private lateinit var binding: FragmentMainBinding
    private var showConnectButton: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        showConnectButton = savedInstanceState?.getBoolean("buttonState") ?: true
        binding.disconnectButton.isVisible = !showConnectButton
        binding.connectButton.isVisible = showConnectButton
        val bluetoothController = (requireActivity().application as App).bluetoothController
        val pref = activity?.getSharedPreferences(
            BluetoothConstans.PREFERENCES, Context.MODE_PRIVATE
        )
        val mac = pref?.getString(BluetoothConstans.MAC, "")

        binding.bList.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_deviceListFragment)
        }
        binding.connectButton.setOnClickListener {
            bluetoothController.connect(mac ?: "", this)
        }
        binding.disconnectButton.setOnClickListener {
            bluetoothController.interrupt()
            bluetoothController.closeConnection()
        }
        binding.Sender.setOnClickListener {
            bluetoothController.sendMessage("A")
        }
    }

    override fun onReceive(message: String) {
        requireActivity().runOnUiThread {
            when (message) {
                BluetoothController.BLUETOOTH_CONNECTED -> {
                    showConnectButton = false
                    binding.disconnectButton.isVisible = !showConnectButton
                    binding.connectButton.isVisible = showConnectButton
                }

                BluetoothController.BLUETOOTH_NO_CONNECTED -> {
                    showConnectButton = true
                    binding.disconnectButton.isVisible = !showConnectButton
                    binding.connectButton.isVisible = showConnectButton
                }

                else -> {
                    binding.tvStatus.text = message
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("buttonState", showConnectButton)
    }
}