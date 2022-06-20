package uz.newtech.bluetoothtest.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import uz.newtech.bluetoothtest.model.DeviceInfo

class BTClient(private val context: Context) {

    @SuppressLint("MissingPermission")
    fun getPairedDevicesInfo(): List<DeviceInfo>? {
        try {
            val bluetoothManager =
                context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val devices = bluetoothManager.adapter?.takeIf { it.isEnabled }?.bondedDevices
            if (devices != null) {
                return devices.map { DeviceInfo(name = it.name, address = it.address) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}