package com.example.fusedlocationproviderclient

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.Task
import java.io.FileNotFoundException
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {


    private lateinit var button: Button
    private lateinit var textView: TextView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fileName: String
    private lateinit var fileOutputStream: FileOutputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        button.setOnClickListener() {
            checkPermission()
        }

        locationRequest = LocationRequest().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        var i = 0
        var xLat = 0.0
        var xLon = 0.0
        var yLat = 0.0
        var yLon = 0.0
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    Log.d("Test001", " $i -> ${location.latitude}, ${location.longitude}, $location")
                    if (i == 0){
                        xLat = location.latitude
                        xLon = location.longitude
                    }
                    yLat = location.latitude
                    yLon = location.longitude
                    val d = distFrom(xLat, xLon, yLat, yLon)
                    if(d > 100.0){
                        val msg = "100 metros ? $d ? - from ($xLat, $xLon) to ($yLat, $yLon)"
                        Log.d("Test002", msg)
                        write(msg)
                        xLat = yLat
                        xLon = yLon
                    }
                    i++
                }
            }
        }

        fileName = "FusedLocationProviderClientUserPath.txt"

        fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)

    }

    override fun onStart() {
        super.onStart()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        else{
            //getLocations()
            checkSettingsAndStartLocationUpdates()
        }

    }

    override fun onStop(){
        super.onStop()
        stopLocationUpdates()
    }

    private fun checkSettingsAndStartLocationUpdates(){
        val request = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseBack: Task<LocationSettingsResponse> = client.checkLocationSettings(request.build())

        locationSettingsResponseBack.addOnSuccessListener{
            startLocationUpdates()
        }

        locationSettingsResponseBack.addOnFailureListener {
            if (it is ResolvableApiException) {
                val apiException: ResolvableApiException = it
                apiException.startResolutionForResult(this, 1001)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun checkPermission() {
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocations() {
        fusedLocationProviderClient.lastLocation?.addOnSuccessListener {
            if(it==null){
                Toast.makeText(this,"Lo siento, no puedo obtener la ubicación",Toast.LENGTH_SHORT).show()
            }
            else it.apply {
                val latitude = it.latitude
                val longitude = it.longitude
                Log.d("Test", "Latitude: $latitude,longitude: $longitude")
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 1){
            if(grantResults.isNotEmpty()&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permiso concedido", Toast.LENGTH_SHORT).show()
                //getLocations()
                checkSettingsAndStartLocationUpdates()
            }
            else{
                Toast.makeText(this,"Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun write(_text: String){

        try {
            fileOutputStream.write(_text.toByteArray())
            fileOutputStream.write(10)
            Log.d("Test003", "Guardado con éxito $_text")
        }
        catch (e: FileNotFoundException){
            e.printStackTrace()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun distFrom(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Float {
        val earthRadius = 6371000.0 // metros
        val dLat = Math.toRadians((lat2 - lat1))
        val dLng = Math.toRadians((lng2 - lng1))
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(
            Math.toRadians(lat2)
        ) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c =
            2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (earthRadius * c).toFloat()
    }

}

