package com.example.locationmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }
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
        //btnGetLocation.setOnClickListener {  }


    }

    //Function extention for Toast.makeText(...)
    fun Context.showToast (text: CharSequence, duration: Int= Toast.LENGTH_SHORT){
        Toast.makeText(this, text, duration).show()
    }
}