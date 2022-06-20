package uz.newtech.bluetoothtest.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import uz.newtech.bluetoothtest.R
import uz.newtech.bluetoothtest.core.Constants.APP_TAG
import uz.newtech.bluetoothtest.data.BTClient

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

}