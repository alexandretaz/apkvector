package br.com.hypersites.grupovector.vipcard


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() , LocationListener{
    private val RECORD_REQUEST_CODE = 101
    private val TAG = "teste location"
    val REQUEST_LOCATION = 2
    var alarm :Alarm? = null
    var locaManager : LocationManager? = null
    private var createAlarmTask: alarmCreationTask? = null


    var latitude:Double = 0.00
    var longitude:Double = 0.00
    var status = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setLocation()
        callAlarmServer()
        this.status=1


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun callAlarmServer() {
        Handler().postDelayed({
            Log.i("Criar Alarme:","Entrou")
            if(this.status!=0){
                val __connection = SqliteHelper.getInstance(applicationContext)
                val client = __connection.getUser()
                createAlarmOnServer(client.token,client.device_id, this.latitude, this.longitude)
                startAlarm(__connection,client)

            }
        }, 10000)

    }


     fun startAlarm(_connection:SqliteHelper, client:AppClient) {
        val _alarm:Alarm = Alarm()
        _alarm.setConnection(_connection)

        _alarm.setPosition(this.latitude,this.longitude)
        _alarm.setClient(client)
        //_alarm.setLManager(this.locaManager!!)
        this.alarm = _alarm
    }


    private fun setLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        !=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        , REQUEST_LOCATION)
        }
        else{
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            val provider = locationManager.getBestProvider(criteria, false)
            val location = locationManager.getLastKnownLocation(provider)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,10f,this)
            if(location != null) {
                this.latitude = location.latitude
                this.longitude = location.longitude
                if(locationManager!=null && this.alarm != null) {
                    this.alarm?.setLManager(locationManager)
                    val _connection = SqliteHelper.getInstance(applicationContext)
                    val client = _connection.getUser()

                }

            }else{
                Toast.makeText(this, "Localização não disponível", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onProviderDisabled(provider: String?) {
        intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }
    override fun onLocationChanged(p0:Location?) {
        setLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
       if(requestCode == REQUEST_LOCATION)
       {
           setLocation()
       }
    }
    fun createAlarmOnServer(token:String, device_id:String,latitude: Double, longitude: Double):Boolean
    {


        if (createAlarmTask!= null) {
            return false
        }

        var cancel = false



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
        } else {


            val __connection = SqliteHelper.getInstance(applicationContext)
            val client = __connection.getUser()
            createAlarmTask = alarmCreationTask(token, device_id, latitude, longitude, __connection )
            createAlarmTask!!.execute(null as Void?)

            startAlarm(__connection,client)
            return true
        }
        return cancel
    }




    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                if(this.status!=0) {
                    intent = Intent(this, Alarm::class.java)
                    stopService(intent)
                    this.status = 0
                }
                else{
                    intent = Intent(this, Alarm::class.java)
                    startService(intent)
                    this.status=1;
                    callAlarmServer()
                }

            }
            R.id.navigation_dashboard -> {
               intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            }
        }
        false
    }

    inner class alarmCreationTask internal constructor(private val token: String, private val imei: String, private val latitude: Double, private val longitude:Double,val __connection: SqliteHelper?) : AsyncTask<Void, Void, Boolean>() {



        override fun doInBackground(vararg params: Void?): Boolean {
            val token = token
            var imei = imei
            val latitude=latitude
            val longitude = longitude
            try {
                val json = JSONObject()
                json.put("token",token)
                json.put("latitude", latitude.toString() )
                json.put("longitude", longitude.toString() )
                json.put("imei",imei)
                Log.i("Request", json.toString())
                val (request, resquestBody, result) = Fuel.post("https://vipcard.grupovector.com.br:3278/api/V1/alarm")
                        .body(json.toString())
                        .responseString()
                result.fold({
                    val jsonText  = result.toString().removePrefix("[Success: ").removeSuffix("]")

                    val alarmJson = JSONObject(jsonText)
                    __connection!!.registerAlarm(alarmJson.getInt("id"))

                }, {Log.i("Ao criar alarm",resquestBody.toString())})

                return true

            } catch (e: InterruptedException) {

                return false
            }

            return  false
        }

    }



}
