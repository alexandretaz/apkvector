package br.com.hypersites.grupovector.vipcard

/**
 * Created by alexandre on 23/03/18.
 */
data class AppClient(val token: String, val device_id: String) {
    companion object {
        val TABLE_NAME = "user_access"
        val COLUMN_TOKEN = "token"
        val COLUMN_DEVICE_ID = "device_id"
    }
}