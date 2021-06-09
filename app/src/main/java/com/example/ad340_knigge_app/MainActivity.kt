package com.example.ad340_knigge_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            inputEmail = findViewById(R.id.email_input)
            inputPassword = findViewById(R.id.password_input)
            inputUsername = findViewById(R.id.username_input)

            // validate inputs
            if(areInputsValid()){
                // if valid run login
                signIn()
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

    fun areInputsValid(): boolean {
        val email = isEmailValid(inputEmail.toString())
        val username = inputUsername.toString().isEmpty()
        val password = inputPassword.toString().isEmpty()
        return email && username && password
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

    fun saveToSharedPreferences(email: String, username:String, password:String){
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()){
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

    fun signIn(){
        Log.d("FIREBASE", "signIn")

        // 2 - save valid entries to shared preferences


        // 3 - sign into Firebase
        val mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(inputEmail.toString(), inputPassword.toString())
                .addOnCompleteListener(this,


                        new OnCompleteListener < AuthResult >() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("FIREBASE", "signIn:onComplete:" + task.isSuccessful())

                        if (task.isSuccessful()) {
                            // update profile. displayname is the value entered in UI
                            FirebaseUser user = FirebaseAuth . getInstance ().getCurrentUser()
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayname)
                                    .build()

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener < Void >() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("FIREBASE", "User profile updated.")
                                                // Go to FirebaseActivity
                                                startActivity(new Intent MainActivity.this, FirebaseActivity.class))
                                            }
                                        }
                                    })

                        } else {
                            Log.d("FIREBASE", "sign-in failed")

                            Toast.makeText(MainActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }


}