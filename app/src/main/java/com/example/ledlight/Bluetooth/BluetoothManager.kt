package com.example.ledlight.Bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.ledlight.util.Constants.Companion.MESSAGE_TOAST
import com.example.ledlight.util.Constants.Companion.MESSAGE_WRITE
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothManager() {
    private val TAG = "BLUETOOTH"
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var pairedDevices: Set<BluetoothDevice> = emptySet()
    private val handler = Handler()
    private var connected: MutableLiveData<Boolean> = MutableLiveData()
    private var connectThread : ConnectThread? = null


    init {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled) {
                //val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                //startActivityForResult(context as Activity, enableBtIntent, Constants.REQUEST_ENABLE_BT, null)
            }
            pairedDevices = bluetoothAdapter.bondedDevices
        } else {
            Log.e(TAG, "This device doesnt support BlueTooth")
        }
        connected.value = false
    }

    fun getDevices() = pairedDevices

    fun connectTo(mac: String) : MutableLiveData<Boolean> {
        connectThread = bluetoothAdapter?.getRemoteDevice(mac)?.let { ConnectThread(it) }
        connectThread?.start()
        return connected
    }

    fun disconnect(){
        connectThread?.cancel()
    }

    fun send(text : String){
        connectThread?.send(text.toByteArray())
    }

    private inner class ConnectThread(btdevice: BluetoothDevice) : Thread() {
        private val mmInStream: InputStream? by lazy { mmSocket?.inputStream  }
        private val mmOutStream: OutputStream? by lazy{ mmSocket?.outputStream }
        private val mmBuffer: ByteArray by lazy { ByteArray(1024) }
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            btdevice.createRfcommSocketToServiceRecord(btdevice.getUuids().get(0).getUuid())
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()
            try {
                mmSocket?.use { socket ->
                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.
                    socket.connect()
                    //var connectedThread = ConnectedThread(socket)
                    connected.postValue(true)
                    receive()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Could not open client socket", e)
            }
        }

        fun receive(){
            var numBytes: Int // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                try {
                    if(mmInStream != null){
                        numBytes = mmInStream!!.read(mmBuffer)
                    }else{
                        numBytes = 0
                    }
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }
/*
                // Send the obtained bytes to the UI activity.
                val readMsg = handler.obtainMessage(
                    MESSAGE_READ, numBytes, -1,
                    mmBuffer
                )
                readMsg.sendToTarget()

 */
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun send(bytes: ByteArray) {
            try {
                mmOutStream?.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            // Share the sent message with the UI activity.
            val writtenMsg = handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, mmBuffer
            )
            writtenMsg.sendToTarget()
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            if (connected.value!!) {
                try {
                    mmSocket?.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Could not close the client socket", e)
                }finally {
                    connected.postValue( false)
                }
            }
        }
    }
}