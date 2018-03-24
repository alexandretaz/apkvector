package br.com.hypersites.grupovector.vipcard

/**
 * Created by alexandre on 23/03/18.
 */
data class AppClient(val token: String, val device_id: String) {
    companion object {
        val TABLE_NAME = "user_access"
        val COLUMN_ID = "id"
        val COLUMN_TOKEN = "token"
        val COLUMN_DEVICE_ID = "device_id"
    }
    override fun toString():String {
        val string = "Token:$token Device_id:$device_id"
        return string
    }
}