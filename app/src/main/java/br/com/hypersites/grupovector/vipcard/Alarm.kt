package br.com.hypersites.grupovector.vipcard

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import AlarmPoint
import android.annotation.SuppressLint


import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject


class Alarm: Service() {

    var locationManager:LocationManager? =null
    var latitude:Double = 0.00
    var id:Int = 0
    var token:String = ""
    var imei:String = ""
    var status = 1
    var longitude:Double = 0.00
    var __connection:SqliteHelper?=null
    private var createAlarmTask: alarmPoint? = null

    override fun onBind(intent: Intent): IBinder? {
        // Used only in case of bound services.
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()

        val criteria = Criteria()
        this.locationManager =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = locationManager?.getBestProvider(criteria, false)
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.00f, locationListener)
        Log.i("Serviço Alarme:", "Entrou")

    }

    private val locationListener:LocationListener = object :LocationListener{
        override fun onLocationChanged(location: Location?) {
            setPosition(location!!.latitude,location!!.longitude)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    fun setConnection(dbConnection:SqliteHelper) {
        this.__connection = dbConnection;
    }



    fun setLManager(locationManager: LocationManager){
        this.locationManager = locationManager
    }

    fun setClient(client:AppClient) {
        var client:AppClient = client
        val token = client.token
        this.token = token
        val device_id = client.device_id
        this.imei = device_id
    }

    fun run(token:String,imei:String):Int{
        if(this.status!=0){
            send(token, imei)
        }
        return 1
    }

    fun send(token:String, imei:String):Int{
        Handler().postDelayed({
            Log.i("Criar Alarme:","Entrou")
            pointAlarmOnServer(token,imei,this.latitude,this.longitude)
        }, 30000)
        return 1
    }

    fun setPosition(latitude:Double, longitude:Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
    private fun runtime_permissions():Boolean {
        if(Build.VERSION.SDK_INT>=23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this.locationManager!=null){
            locationManager?.removeUpdates(locationListener)
        }
    }


    fun pointAlarmOnServer(token:String, device_id:String,latitude: Double, longitude: Double):Boolean
    {
        Log.i("Mandando Posição", "Chamou metodo")

        if (createAlarmTask!= null) {
            return false
        }

        var cancel = false



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
        } else {


            createAlarmTask = alarmPoint(token, device_id, latitude, longitude, __connection)
            createAlarmTask!!.execute(null as Void?)
            if(this.status!=0){

            }
            return true
        }
        return cancel
    }

    fun destroy() {
        super.onDestroy()
        this.status=0
        createAlarmTask!!.cancel(true)
        Log.i("Alarm Finalizado:", " finalizado")
    }




    inner class alarmPoint internal constructor(private val token: String, private val imei: String, private val latitude: Double, private val longitude:Double,val __connection: SqliteHelper?) : AsyncTask<Void, Void, Boolean>() {



        override fun doInBackground(vararg params: Void?): Boolean {
                val token = token
                var imei = imei
                val latitude=latitude
                val longitude = longitude
                try {
                    val json = JSONObject()
                    var resultHttp = false
                    json.put("token",token)
                    json.put("latitude", latitude.toString() )
                    json.put("longitude", longitude.toString() )
                    json.put("imei",imei)
                    Log.i("Request", json.toString())
                val (request, resquestBody, result) = Fuel.post("https://vipcard.grupovector.com.br:3278/api/V1/alarm")
                    .body(json.toString())
                        .responseString()
                result.fold({
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
