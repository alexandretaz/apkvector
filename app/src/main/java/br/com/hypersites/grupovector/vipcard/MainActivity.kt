package br.com.hypersites.grupovector.vipcard

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {
    private val RECORD_REQUEST_CODE = 101

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
               intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val _connection = SqliteHelper.getInstance(applicationContext)
        val client = _connection.getUser()
        Log.i("Client",client.toString())
        val locationPermission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coerseLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        var latitude = 0.00
        var longitude = 0.00
        if(locationPermission==PackageManager.PERMISSION_GRANTED || coerseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Service.LOCATION_SERVICE) as LocationManager;

            try{
                latitude = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER).latitude
                longitude = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER).longitude
            } catch (e: Exception ) {
            }

        }
        else{
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    RECORD_REQUEST_CODE)

            Log.i("Sem permissão", "Está sem permissão")
        }
        Log.i("LAtitude", latitude.toString())
        Log.i("Longitude", longitude.toString())
        val _alarm:Alarm = Alarm()
        _alarm.setPosition(latitude, longitude)
        _alarm.setClient(client)


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                             permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("Permissão", "Permission has been denied by user")
                } else {
                    Log.i("Permissão", "Permission has been granted by user")
                }
            }
        }
    }

}
