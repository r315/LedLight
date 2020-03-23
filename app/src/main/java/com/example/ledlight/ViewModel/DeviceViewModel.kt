package com.example.ledlight.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ledlight.Bluetooth.BluetoothManager
import com.example.ledlight.Model.BlueToothDeviceModel


class DeviceViewModel(private val context : Application) : AndroidViewModel(context){
    private val TAG : String = "DEVICE VIEW MODEL"
    private val deviceNamesList : MutableLiveData<List<BlueToothDeviceModel>> = MutableLiveData()
    private val bluetooth by lazy { BluetoothManager() }

    init {
        val pairedDeviceList : MutableList<BlueToothDeviceModel> = mutableListOf()

        bluetooth.getDevices().forEach { device ->
            val dev = BlueToothDeviceModel(device.name, device.address)
            pairedDeviceList.add(dev)
        }

      /*  for(device in (1..10)){
            var dev = BlueToothDeviceModel("Device $device", "MAC: $device")
            pairedDeviceList.add(dev)
        }
*/
        deviceNamesList.value = pairedDeviceList
    }

    fun getDevices() : LiveData<List<BlueToothDeviceModel>> {
        return deviceNamesList
    }
}