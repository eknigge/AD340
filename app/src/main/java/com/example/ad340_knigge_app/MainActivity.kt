package com.example.ad340_knigge_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    var mCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Link to movie activity
        var movieButton = findViewById<Button>(R.id.movies_button)
        movieButton.setOnClickListener {
            val intent = Intent(this , Movies::class.java)
            startActivity(intent)
        }
    }

    fun sendToast(view: View) {
        Toast.makeText(this,"Message Sent",Toast.LENGTH_SHORT).show()
    }

    fun runNetflix(view: View) {
        Toast.makeText(this, "Run Netflix", Toast.LENGTH_LONG).show()
    }

    fun runHulu(view: View) {
        Toast.makeText(this, "Run Hulu", Toast.LENGTH_LONG).show()
    }

    fun runDisney(view: View) {
        Toast.makeText(this, "Run Disney", Toast.LENGTH_LONG).show()
    }

    fun runLiveTV(view: View) {
        Toast.makeText(this, "Watch Live TV", Toast.LENGTH_LONG).show()
    }


}