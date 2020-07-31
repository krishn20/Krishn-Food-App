package com.internshala.krishnfoodapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.internshala.krishnfoodapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            val startAct = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(startAct)
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}