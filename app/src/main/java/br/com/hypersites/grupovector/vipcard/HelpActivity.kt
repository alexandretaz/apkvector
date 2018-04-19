package br.com.hypersites.grupovector.vipcard

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import com.github.nkzawa.engineio.client.Socket
import com.github.nkzawa.socketio.client.IO
import kotlinx.android.synthetic.main.activity_help.*
import org.jetbrains.anko.longToast
import org.w3c.dom.Text

class HelpActivity : AppCompatActivity(){




    //private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
       val btn_call = findViewById<RecyclerView>(R.id.button_chatbox_send) as Button
        val messageChat = findViewById<RecyclerView>(R.id.edittext_chatbox) as EditText
        btn_call.setOnClickListener {
            runtime_permissions()
            longToast("O servidor de chat não está no ar, por favor entre em contato por telefone!")


        }
    }




    private fun runtime_permissions():Boolean {
        var location:Int = 0
        var phone:Int = 0
        var total:Int = 0
        if(Build.VERSION.SDK_INT>=23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    , 100)
             location = 1
        }

        if(Build.VERSION.SDK_INT>=23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CALL_PHONE)
                    , 100)
            phone = 1
        }
        total = (location+phone)
        if(total==2) {
            return true
        }
        return false
    }


}
