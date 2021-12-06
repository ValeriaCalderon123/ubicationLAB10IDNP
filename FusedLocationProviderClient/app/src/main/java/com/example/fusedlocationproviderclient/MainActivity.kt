package com.example.fusedlocationproviderclient

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var  button: Button
    private lateinit var textView: TextView
    private lateinit var  fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        button.setOnClickListener() {
            checkPermission()
        }
    }

    private fun checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
        else{
            getLocations()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocations() {
        fusedLocationProviderClient.lastLocation?.addOnSuccessListener {
            if(it==null){
                Toast.makeText(this,"Sorry Can't get Location",Toast.LENGTH_SHORT).show()
            }
            else it.apply { {
                val latitude = it.latitude
                val longitude = it.longitude
                textView.text   = "Latitude: $latitude,longitude: $longitude"
            } }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode ==1){
            if(grantResults.isNotEmpty()&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show()
                getLocations()
            }
            else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT)
            }
        }



    }
}

