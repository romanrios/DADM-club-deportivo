//package com.grupo1dam.clubdeportivo
//
//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.LinearLayout
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.grupo1dam.clubdeportivo.R
//import com.grupo1dam.clubdeportivo.data.DatabaseHelper
//
//class EjemploActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_ejemplo)
//
//
//        val databaseHelper = DatabaseHelper(this)
//
//        val editNombre = findViewById<EditText>(R.id.editNombre)
//        val editDNI = findViewById<EditText>(R.id.editDNI)
//        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
//
//        val cardShowForm = findViewById<TextView>(R.id.cardShowForm)
//
//        val formLayout = findViewById<LinearLayout>(R.id.formLayout)
//
//
//        cardShowForm.setOnClickListener {
//            formLayout.visibility = View.VISIBLE
//            cardShowForm.visibility = View.GONE
//        }
//
//        btnGuardar.setOnClickListener {
//            val nombre = editNombre.text.toString().trim()
//            val dni = editDNI.text.toString().trim()
//
//            if(databaseHelper.insertarSocio(nombre,dni)){
//                Toast.makeText(this, "com.grupo1dam.clubdeportivo.data.Socio agregado", Toast.LENGTH_SHORT).show()
//                editNombre.text.clear()
//                editDNI.text.clear()
//            } else {
//                Toast.makeText(this, "Error al agregar socio", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//
//        }
//
//
//    }
//
