package br.com.hypersites.grupovector.vipcard

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.util.Log
import android.*
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat.requestPermissions


/**
 * Created by alexandre.andrade on 21/03/18.
 */
class  Help : Service() {

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG, " finalizado")
    }

    override fun onBind(intent: Intent): IBinder? {
        // Used only in case of bound services.
        return null
    }



    companion object {
        private val LOG_TAG = "Vipcard - solicitação"
    }
}