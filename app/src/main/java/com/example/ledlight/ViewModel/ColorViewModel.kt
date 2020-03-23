package com.example.ledlight.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ledlight.Bluetooth.BluetoothManager

class ColorViewModel (private val context : Application) : AndroidViewModel(context){
    private val bluetooth by lazy { BluetoothManager() }

    init {

    }

    fun connectTo(mac : String) = bluetooth.connectTo(mac)

    fun disconnect(){
        bluetooth.disconnect()
    }

    fun setColor(red : Int, green : Int , blue : Int){
        bluetooth.send("rgb $red $green $blue\r")
    }
}