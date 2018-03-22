package br.com.hypersites.grupovector.vipcard

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject
import java.util.Timer

/**
 * Created by alexandre.andrade on 21/03/18.
 */
class CallTimer: Timer() {



    fun setTime(seconds:Int = 10) {
        val secondsToAction = seconds
    }



    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val request = JSONObject();

            request.put("longitude", location.longitude)
            request.put("latitude", location.latitude)
            val (ignoredRequest, ignoredResponse, result) = Fuel.post("https://vipcard.grupovector.com.br:3278/api/V1/alarm/point/add")
                    .body(request.toString())
                    .responseString()
            result.fold({ /*success*/ },
                    { /*failure*/ }
            )

        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

}