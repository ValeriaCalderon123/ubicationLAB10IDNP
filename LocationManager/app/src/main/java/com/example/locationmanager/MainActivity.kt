package com.example.locationmanager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.StringBuilder
import java.util.*
import androidx.constraintlayout.motion.widget.Debug.getLocation




class MainActivity : AppCompatActivity(), LocationListener {

    var locationManager: LocationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 100
            )
        }


    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {
            locationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                5f,
                this@MainActivity
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    override fun onLocationChanged(location: Location) {
        Toast.makeText(this, "" + location.latitude + "," + location.longitude, Toast.LENGTH_SHORT)
            .show()
        try {
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses[0].getAddressLine(0)
            val array = ArrayList<String>()
            var latitudString = location.latitude
            var longitudeString= location.longitude
            //   val coordenadas=latitudString+"-"+longitudeString
            for (i in 0..39) {
                //equivale 100 metros en grados
                latitudString = latitudString+0.0009
                longitudeString =longitudeString+0.0009
                array.add((longitudeString.toString())+"-"+(latitudString.toString())+"\n")
            }
            Log.d("ARRAY",array.toString());
            editData.setText(array.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    fun initUI(){
        val fileName = "myPath.txt"
        // Para escribir y guardar el contenido del edit text en un archivo.

        btnWrite.setOnClickListener{
            val data = editData.text.toString()

            val fileOutputStream: FileOutputStream

            try {
                fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
                fileOutputStream.write(data.toByteArray())
            }
            catch (e: FileNotFoundException){
                e.printStackTrace()
            }
            catch (e: Exception){
                e.printStackTrace()
            }

            showToast("Guardado con éxito")
        }

        //Para leer y mostrar contenido del archivo
        btnRead.setOnClickListener {
            var fileInputStream: FileInputStream? = null
            fileInputStream = openFileInput(fileName)

            var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)

            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while ({text = bufferedReader.readLine(); text}() !=null){
                stringBuilder.append(text)
            }

            editData.setText(stringBuilder.toString()).toString()
        }


        btnClear.setOnClickListener { editData.setText("") }

        btnGetLocation.setOnClickListener({ getLocation() })


    }

    //Function extention for Toast.makeText(...)
    fun Context.showToast (text: CharSequence, duration: Int= Toast.LENGTH_SHORT){
        Toast.makeText(this, text, duration).show()
    }
}