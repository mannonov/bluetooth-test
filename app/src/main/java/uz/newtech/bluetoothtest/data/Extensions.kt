package uz.newtech.bluetoothtest.data

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import uz.newtech.bluetoothtest.core.Constants.RC_PERMISSION_FOR_DISCOVERY

fun Context.isGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun <T> MutableLiveData<List<T>>.mutateList(block: MutableList<T>.() -> Unit) {
    value?.let {
        val mutableList = it.toMutableList()
        block(mutableList)
        value = mutableList.toList()
    }
}


private val mRequiredPermissions = arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_ADMIN
)

fun FragmentActivity.checkRequiredBluetoothPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val grants = mRequiredPermissions.map { this.isGranted(it) }
        if (grants.any { !it }) {
            ActivityCompat.requestPermissions(
                this,
                mRequiredPermissions,
                RC_PERMISSION_FOR_DISCOVERY
            )
            return
        }
    }
}
