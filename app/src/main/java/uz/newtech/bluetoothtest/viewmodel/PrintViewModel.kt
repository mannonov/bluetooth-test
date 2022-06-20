package uz.newtech.bluetoothtest.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.newtech.bluetoothtest.data.BTClient
import javax.inject.Inject

@HiltViewModel
class PrintViewModel @Inject constructor(private val client: BTClient) : ViewModel() {

    private var _pairedDevicesInfoLiveData: MutableLiveData<List<BluetoothDevice>> =
        MutableLiveData()
    val pairedDevicesInfoLiveData: LiveData<List<BluetoothDevice>> get() = _pairedDevicesInfoLiveData

    fun printContent(device: BluetoothDevice, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            client.immediatePrintToDevice(device, message = content)
        }
    }

    fun getPairedDevicesInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            _pairedDevicesInfoLiveData.postValue(client.getPairedDevicesInfo())
        }
    }

}