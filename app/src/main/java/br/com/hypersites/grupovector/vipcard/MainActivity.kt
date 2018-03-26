package br.com.hypersites.grupovector.vipcard

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.jar.Manifest
import AlarmPoint


class MainActivity : AppCompatActivity() {
    private val RECORD_REQUEST_CODE = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient


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
        val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        var latitude:Double = 0.00
        var longitude:Double = 0.00
        if(locationPermission!=PackageManager.PERMISSION_GRANTED){
            Log.e("Location","Unable to acess location")
        }
        else {
            mFusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if(location !=null) {
                            Log.i("LOcation LIstener", "Ouvindo Localização")
                            latitude = location.latitude
                            longitude = location.longitude
                        }
                        else{
                            Log.e("LOcation LIstener", "To surdo")
                        }
                    }
            Log.i("Latitude", latitude.toString())
            Log.i("Longitude", longitude.toString())
        }
        val _alarm:Alarm = Alarm()
        _alarm.setConnection(_connection)
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
