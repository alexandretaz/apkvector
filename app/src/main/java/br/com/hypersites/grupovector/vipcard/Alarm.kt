package br.com.hypersites.grupovector.vipcard

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import AlarmPoint
import android.Manifest
import android.app.Activity

import android.location.LocationListener
import android.os.*
import android.support.v4.app.ActivityCompat

import android.support.v4.content.ContextCompat
import com.github.kittinunf.fuel.Fuel
import com.google.android.gms.location.LocationServices
import org.json.JSONObject


class Alarm: Service() {
    private var createAlarmTask: alarmCreationTask? = null
    var id:Int = 0

    var latitude = 0.00

    var longitude = 0.00
    var __connection:SqliteHelper?=null

    fun setConnection(dbConnection:SqliteHelper) {
        this.__connection = dbConnection;
    }
    fun setClient(client:AppClient) {
        var client:AppClient = client
        val token = client.token
        val device_id = client.device_id
        Handler().postDelayed({
            createAlarmOnServer(token,device_id)
        }, 10000)
    }



    fun createAlarmOnServer(token:String, device_id:String):Boolean
    {

        val _alarmPoint: AlarmPoint = AlarmPoint()
        if (createAlarmTask!= null) {
            return false
        }

        var cancel = false



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            createAlarmTask = alarmCreationTask(token, device_id, __connection)
            createAlarmTask!!.execute(null as Void?)
            return true
        }
        return cancel
    }

    fun destroy() {
        super.onDestroy()
        Log.i("Alarm Finalizado:", " finalizado")
    }

    override fun onBind(intent: Intent): IBinder? {
        // Used only in case of bound services.
        return null
    }


    inner class alarmCreationTask internal constructor(private val token: String, private val imei: String, val __connection: SqliteHelper?) : AsyncTask<Void, Void, Boolean>() {



        override fun doInBackground(vararg params: Void?): Boolean {
                val token = token
                var imei = imei
                try {
                    val json = JSONObject()
                    var resultHttp = false
                    json.put("token",token)
                    json.put("latitude", latitude )
                    json.put("longitude", longitude )
                    json.put("imei",imei)
                    Log.i("Request", json.toString())
                val (request, resquestBody, result) = Fuel.post("https://vipcard.grupovector.com.br:3278/api/V1/alarm")
                    .body(json.toString())
                        .responseString()
                result.fold({
                    Log.i("Teste Alarm", result.toString())
                    resultHttp = true
                    val jsonText  = result.toString().removePrefix("[Success: ").removeSuffix("]")
                    val alarmJson = JSONObject(jsonText)

                    __connection!!.registerAlarm(alarmJson.getInt("id"))

                }, {Log.i("Ao criar alarm",resquestBody.toString())})

                return resultHttp

            } catch (e: InterruptedException) {

                return false
            }

            return  false
        }

    }
}
