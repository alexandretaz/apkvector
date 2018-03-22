package br.com.hypersites.grupovector.vipcard

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log

import android.provider.SyncStateContract
import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject



class Alarm: Service() {



     fun onCreate( timer: CallTimer) {
        super.onCreate()
        timer.setTime(30)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val postParams =  JSONObject()
        val token = "aaaa"
        postParams.put("token",token)
        val (request, body,response)=Fuel.post("https://vipcard.grupovector.com.br:3278/api/V1/alarm/create").body(postParams.toString()).response()
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
        private val LOG_TAG = "Vipcard - online"
    }
}
