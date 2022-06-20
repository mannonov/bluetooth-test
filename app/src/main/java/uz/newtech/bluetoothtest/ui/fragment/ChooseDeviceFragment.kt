package uz.newtech.bluetoothtest.ui.fragment

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import uz.newtech.bluetoothtest.R
import uz.newtech.bluetoothtest.adapter.BTDeviceAdapter
import uz.newtech.bluetoothtest.core.Constants.APP_TAG
import uz.newtech.bluetoothtest.data.BTClient
import uz.newtech.bluetoothtest.databinding.FragmentChooseDeviceBinding
import javax.inject.Inject

@AndroidEntryPoint
class ChooseDeviceFragment : Fragment(R.layout.fragment_choose_device) {

    private val binding: FragmentChooseDeviceBinding by viewBinding(FragmentChooseDeviceBinding::bind)
    private var adapter: BTDeviceAdapter? = null
    private var choosenDevice: BluetoothDevice? = null

    @Inject
    lateinit var client: BTClient

    private val bluetoothOffAlertDialog = lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.bluetooth_turned_off_alert)
            .setPositiveButton(android.R.string.ok, null)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BTDeviceAdapter()
        binding.selectBt.setAdapter(adapter)
        binding.selectBt.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                binding.btnPrint.isEnabled = true
                choosenDevice = adapter?.getItem(position)
            }
        client.checkRequiredBluetoothPermissions(requireActivity())
        updateBluetoothDevices()

        binding.btnPrint.setOnClickListener {
            Log.d(APP_TAG, "onViewCreated: $choosenDevice")
            choosenDevice?.let { it1 ->
                client.immediatePrintToDevice(
                    it1,
                    binding.edtContent.text.toString()
                )
            }
        }

    }

    private fun updateBluetoothDevices() {
        var devicesInfo = client.getPairedDevicesInfo()

        if (devicesInfo == null) {
            bluetoothOffAlertDialog.value.show()
            devicesInfo = emptyList()
        } else {
            if (bluetoothOffAlertDialog.isInitialized()) {
                bluetoothOffAlertDialog.value.dismiss()
            }
        }

        binding.selectBtContainer.error = if (devicesInfo.isEmpty()) {
            getString(R.string.no_devices_found)
        } else {
            null
        }

        adapter?.updateItems(devicesInfo)
    }

}