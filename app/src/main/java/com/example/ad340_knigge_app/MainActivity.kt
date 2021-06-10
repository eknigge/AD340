package com.example.ad340_knigge_app

import android.R.attr.password
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class MainActivity : AppCompatActivity() {
    var mCount = 0
    lateinit var inputUsername: String
    lateinit var inputEmail: String
    lateinit var inputPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Link to movie activity
        var movieButton = findViewById<Button>(R.id.movies_button)
        movieButton.setOnClickListener {
            val intent = Intent(this, Movies::class.java)
            startActivity(intent)
        }

        // Link to traffic activity
        var trafficButton = findViewById<Button>(R.id.traffic_camera_button)
        trafficButton.setOnClickListener {
            val intent = Intent(this, TrafficCameras::class.java)
            startActivity(intent)
        }

        // Link to Camera Map activity
        var cameraMapButton = findViewById<Button>(R.id.camera_map_button)
        cameraMapButton.setOnClickListener{
            Log.d("LOG", "Progress item")
            val intent = Intent(this, CameraMap::class.java)
            startActivity(intent)
        }

        var loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener{

            // initialize input variables
            inputEmail = findViewById<EditText>(R.id.email_input).text.toString()
            inputPassword = findViewById<EditText>(R.id.password_input).text.toString()
            inputUsername = findViewById<EditText>(R.id.username_input).text.toString()

            // validate inputs
            if(areInputsValid()){
                FirebaseApp.initializeApp(this)
                altsignIn()
//                signIn()
            }
        }

    }

    fun isEmailValid(email: String): Boolean{
        if(email.isEmpty()){
            return false
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    fun areInputsValid(): Boolean {
        val email = isEmailValid(inputEmail.toString())
        val username = !(inputUsername.toString().isEmpty())
        val password = !(inputPassword.toString().isEmpty())
        return (email && username && password)
    }

    fun sendToast(view: View) {
        Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
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

    fun saveToSharedPreferences(email: String, username: String, password: String){
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()){
            putString("email", email)
            putString("username", username)
            putString("password", password)
            apply()
        }
    }

    fun getSharedPreferenceValues(): List<String>{
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val sharedPrefData = ArrayList<String>()
        sharedPrefData.add(sharedPref.getString("email", "").toString())
        sharedPrefData.add(sharedPref.getString("username", "").toString())
        sharedPrefData.add(sharedPref.getString("password", "").toString())
        return sharedPrefData
    }

    fun signIn() {
        Log.d("FIREBASE", "signIn")
        val mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(
            inputEmail,
            inputPassword
        )
                .addOnCompleteListener(
                    this
                ) { task ->
                    Log.d("FIREBASE", "signIn:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        saveToSharedPreferences(
                            inputEmail,
                            inputUsername,
                            inputPassword
                        )
                        // update profile
                        val user = FirebaseAuth.getInstance().currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(inputUsername.toString())
                                .build()
                        user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("FIREBASE", "User profile updated.")
                                        // Go to FirebaseActivity
                                        startActivity(Intent(this, FirebaseActivity::class.java))
                                    }
                                }
                    } else {
                        Log.d("FIREBASE", "sign-in failed")
                    }
                }
    }


    private fun altsignIn() {
        Log.d("FIREBASE", "signIn")

        // 1 - validate display name, email, and password entries


        // 2 - save valid entries to shared preferences


        // 3 - sign into Firebase
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(
                this
            ) { task ->
                Log.d("FIREBASE", "signIn:onComplete:" + task.isSuccessful)
                if (task.isSuccessful) {
                    // update profile. displayname is the value entered in UI
                    val user = FirebaseAuth.getInstance().currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(inputUsername)
                        .build()
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("FIREBASE", "User profile updated.")
                                // Go to FirebaseActivity
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        FirebaseActivity::class.java
                                    )
                                )
                            }
                        }
                } else {
                    Log.d("FIREBASE", "sign-in failed")
                    Toast.makeText(
                        this@MainActivity, "Sign In Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


}