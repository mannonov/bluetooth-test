package uz.newtech.bluetoothtest.model

data class DeviceInfo(
    val name: String,
    val address: String
) {
    override fun toString(): String {
        return name
    }
}