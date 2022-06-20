package uz.newtech.bluetoothtest.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class BTClient(private val context: Context) {

    private val socketConnections: MutableMap<String, ConnectedThread> = mutableMapOf()
    private val defaultUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    private var defaultBluetoothAdapter: BluetoothAdapter? = null
    private val RC_PERMISSION_FOR_DISCOVERY = 712

    @SuppressLint("MissingPermission")
    fun getPairedDevicesInfo(): List<BluetoothDevice>? {
        try {
            val bluetoothManager =
                context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            defaultBluetoothAdapter = bluetoothManager.adapter
            val devices = bluetoothManager.adapter?.takeIf { it.isEnabled }?.bondedDevices
            if (devices != null) {
                return devices.toList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice, secure: Boolean = false) {
        if (socketConnections[device.address]?.isConnected() == true) return
        try {
            pairToDevice(device, secure).also { sock ->
                sock.connect()
                socketConnections[device.address] = ConnectedThread(sock)
                    .also { conn -> conn.start() }
            }
        } catch (e: IOException) {
            try {
                socketConnections[device.address]?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            socketConnections.remove(device.address)
        }
    }

    class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024)
        private val mmIsConnected: AtomicBoolean = AtomicBoolean(false)

        override fun run() {
            mmIsConnected.set(true)
            while (true) {
                try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    mmIsConnected.set(false)
                    break
                }
            }
        }
        fun isConnected(): Boolean {
            return mmIsConnected.get()
        }

        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
            }
        }
        fun close() {
            mmSocket.close()
            mmIsConnected.set(false)
        }

    }

    fun writeMessageToDevice(device: BluetoothDevice, message: String) {
        if (socketConnections.containsKey(device.address)) {
            socketConnections[device.address]?.also { connection ->
                connection.write(message.toByteArray())
            }
        }
    }

    fun disconnectFromDevice(device: BluetoothDevice) {
        socketConnections[device.address]?.close()
    }


    @SuppressLint("MissingPermission")
    fun pairToDevice(
        device: BluetoothDevice,
        secure: Boolean = false
    ): BluetoothSocket {
        defaultBluetoothAdapter?.cancelDiscovery()
        val socket = if (secure) device.createRfcommSocketToServiceRecord(defaultUUID)
        else device.createInsecureRfcommSocketToServiceRecord(defaultUUID)
        return socket
    }

    private val mRequiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH
    )

    fun checkRequiredBluetoothPermissions(context: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val grants = mRequiredPermissions.map { context.isGranted(it) }
            if (grants.any { !it }) {
                ActivityCompat.requestPermissions(
                    context,
                    mRequiredPermissions,
                    RC_PERMISSION_FOR_DISCOVERY
                )
                return
            }
        }
    }

    fun immediatePrintToDevice(device: BluetoothDevice, message: String) {
        connectToDevice(device, true)
        writeMessageToDevice(device, message)
        disconnectFromDevice(device)
    }

}