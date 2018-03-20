package br.com.hypersites.grupovector.vipcard

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import br.com.hypersites.grupovector.vipcard.HelpActivity

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
