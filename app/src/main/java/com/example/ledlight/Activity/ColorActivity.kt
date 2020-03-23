package com.example.ledlight.Activity

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ledlight.R
import com.example.ledlight.ViewModel.ColorViewModel
import com.example.ledlight.util.Constants
import kotlinx.android.synthetic.main.activity_color.*

class ColorActivity : AppCompatActivity() {
    private val TAG = "COLOR"
    private val colorViewModel: ColorViewModel by lazy { ViewModelProviders.of(this).get(ColorViewModel::class.java) }
    private var redSeekBarHandler : SeekBarHandler = SeekBarHandler()
    private var greenSeekBarHandler : SeekBarHandler = SeekBarHandler()
    private var blueSeekBarHandler : SeekBarHandler = SeekBarHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)
        val mac = intent.getStringExtra(Constants.BLUETOOTH_DEVICE)
        colorViewModel.connectTo(mac).observe(this, Observer<Boolean> { deviceStatus(it) })

        redSeekBar.setOnSeekBarChangeListener(redSeekBarHandler)
        greenSeekBar.setOnSeekBarChangeListener(greenSeekBarHandler)
        blueSeekBar.setOnSeekBarChangeListener(blueSeekBarHandler)
    }

    override fun onDestroy() {
        super.onDestroy()
        colorViewModel.disconnect()
    }

    private fun updateColor(){
        colorViewModel.setColor(redSeekBarHandler.getColor(), greenSeekBarHandler.getColor(), blueSeekBarHandler.getColor())
    }

    fun deviceStatus(state : Boolean){
        Toast.makeText(this, if(state) "Connected" else "Disconnected", Toast.LENGTH_SHORT).show()
    }

    inner class SeekBarHandler : SeekBar.OnSeekBarChangeListener{
        private var color : Int = 0

        fun getColor() = color

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            color = progress
            updateColor()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }
}