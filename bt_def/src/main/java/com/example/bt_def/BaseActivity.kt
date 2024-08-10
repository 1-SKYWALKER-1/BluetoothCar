package com.example.bt_def

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BaseActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)//bluetooth_layout
        //initRcView()
        supportFragmentManager.beginTransaction().replace(R.id.PlaceHolder,DeviceListFragment()).commit()
    }

}