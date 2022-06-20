package uz.newtech.bluetoothtest.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import uz.newtech.bluetoothtest.R
import uz.newtech.bluetoothtest.model.DeviceInfo

class BTDeviceAdapter(private var devices: List<BluetoothDevice> = emptyList()) : BaseAdapter(),
    Filterable {

    private val filter = NoFilter()

    override fun getCount(): Int = devices.size

    override fun getItem(position: Int): BluetoothDevice = devices[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("MissingPermission")
    override fun getView(position: Int, containerView: View?, viewGroup: ViewGroup?): View {
        val view = containerView ?: LayoutInflater.from(viewGroup?.context)
            .inflate(R.layout.item_layout, viewGroup, false)
        (view as TextView).text = devices[position].name
        return view
    }

    override fun getFilter(): Filter = filter

    fun updateItems(items: List<BluetoothDevice>) {
        devices = items
        notifyDataSetChanged()
    }

    private inner class NoFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = devices
                count = devices.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

}