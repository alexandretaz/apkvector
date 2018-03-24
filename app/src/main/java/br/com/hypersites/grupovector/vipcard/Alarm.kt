package br.com.hypersites.grupovector.vipcard

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log

import android.provider.SyncStateContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject
import java.util.Timer
import java.util.jar.Manifest
import kotlin.concurrent.schedule


class Alarm: Service() {
    var id:Int = 0

    var latitude = 0.00

    var longitude = 0.00

    fun onCreate(savedInstaceState: Bundle?) {
        super.onCreate()

    }

    fun setClient(client:AppClient) {
        var client:AppClient = client
        val token = client.token
        val device_id = client.device_id
        Handler().postDelayed({
            createAlarmOnServer(token,device_id)
        }, 10000)
    }

    fun setPosition(latitude:Double, longitude:Double)
    {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun setPosition()
    {
        val locationPermission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coerseLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        var latitude =0.00
        var longitude = 0.00
        if(locationPermission==PackageManager.PERMISSION_GRANTED || coerseLocationPermission == coerseLocationPermission) {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager;

            try{
                latitude = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER).latitude
                longitude = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER).longitude
            } catch (e: Exception ) {
                latitude = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).latitude
                longitude = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).longitude
            }
        }

        else {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager;
            latitude = locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).latitude
            longitude = locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).longitude
        }

        this.latitude = latitude
        this.longitude = longitude

    }



    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun createAlarmOnServer(token:String, device_id:String):Boolean
    {

        var resultHttp = false
        if(this.id>=0) {
            try {
                Log.i("Open Alarm", "Iniciando novo alarme")
                val json = JSONObject()

                json.put("token", token)
                json.put("imei", device_id)
                json.put("latitude", this.latitude)
                json.put("longitude", this.longitude)
                Log.i("Dados Alarm", "$token, $device_id, $latitude, $longitude")
                val (request, resquestBody, result) = Fuel.post("https://vipcard.grupovector.com.br:3278/api/V1/alarm")
                        .body(json.toString())
                        .responseString()
                result.fold({
                    Log.i("Iniciando Alarm", result.toString())
                    resultHttp = true
                    val id = result.toString()
                    val connect = SqliteHelper.getInstance(applicationContext)

                    connect.registerAlarm(id.toInt())
                }, { Log.i("Falha ao Iniciar alarm", result.toString()) })

                return resultHttp

            } catch (e: InterruptedException) {

                return false
            }
        }
        return resultHttp
    }






    fun destroy() {
        super.onDestroy()
        Log.i("Alarm Finalizado:", " finalizado")
    }

    override fun onBind(intent: Intent): IBinder? {
        // Used only in case of bound services.
        return null
    }


}
