package com.example.ledlight.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ledlight.Model.BlueToothDeviceModel
import com.example.ledlight.R
import com.example.ledlight.ViewModel.DeviceViewModel
import com.example.ledlight.util.Constants

class DeviceActivity : AppCompatActivity() {

    private val TAG = "DEVICE"
    private val deviceViewModel: DeviceViewModel by lazy { ViewModelProviders.of(this).get(DeviceViewModel::class.java) }
    private val deviceListView by lazy { findViewById<ListView>(R.id.devList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        deviceViewModel
            .getDevices()
            .observe(this, Observer<List<BlueToothDeviceModel>> {
                deviceListView.adapter = createAdapter(it) //DeviceViewAdapter(it)
                deviceListView.setOnItemClickListener{ _, _, position, _ ->
                    val intent = Intent(applicationContext, ColorActivity::class.java)
                    intent.putExtra(Constants.BLUETOOTH_DEVICE, it.get(position).mac)
                    startActivity(intent)
                }
            })
    }

    private fun createAdapter(teamsList: List<BlueToothDeviceModel>): ListAdapter? {
        return SimpleAdapter(
            this,
            teamsList.map { mapOf("name" to it.name, "desc" to it.mac) },
            android.R.layout.simple_list_item_2,
            arrayOf("name", "desc"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
    }

    /*
    private class DeviceViewAdapter(private val dataset: List<BlueToothDeviceModel>) :
        RecyclerView.Adapter<DeviceViewAdapter.DeviceViewHolder>(){

        inner class DeviceViewHolder(val linearLayout : LinearLayout) : RecyclerView.ViewHolder(linearLayout)

        override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : DeviceViewAdapter.DeviceViewHolder{
            val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_main_device_holder, parent, false) as LinearLayout

            return DeviceViewHolder(layout)
        }

        override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            holder.linearLayout.findViewById<TextView>(R.id.deviceName).text = dataset.get(position).name
            holder.linearLayout.findViewById<TextView>(R.id.deviceMac).text = dataset.get(position).mac
            if(position % 2 == 0){
                holder.linearLayout.setBackgroundColor(Color.CYAN)
            }else{
                holder.linearLayout.setBackgroundColor(Color.BLUE)
            }
        }

        override fun getItemCount(): Int {
            return dataset.count()
        }
    }
*/

}
