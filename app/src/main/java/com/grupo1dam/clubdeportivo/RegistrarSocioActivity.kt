package com.grupo1dam.clubdeportivo

import com.grupo1dam.clubdeportivo.utils.showDatePicker

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1dam.clubdeportivo.base.BaseActivity
import com.grupo1dam.clubdeportivo.utils.DatabaseHelper
import com.grupo1dam.clubdeportivo.utils.RegistroHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistrarSocioActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbarNavigation()

        // Referencias UI
        val etNombre = findViewById<EditText>(R.id.registrarSocio_et_nombre)
        val etApellido = findViewById<EditText>(R.id.registrarSocio_et_apellido)
        val etDni = findViewById<EditText>(R.id.registrarSocio_et_dni)
        val etFechaNacimiento = findViewById<EditText>(R.id.registrarSocio_et_fechaNacimiento)
        val etFechaInscripcion = findViewById<EditText>(R.id.registrarSocio_et_fechaInscripcion)
        val cbAptoFisico = findViewById<CheckBox>(R.id.registrarSocio_cb_aptoFisico)
        val btnAceptar = findViewById<Button>(R.id.registrarSocio_btn_aceptar)
        val btnLimpiar = findViewById<Button>(R.id.registrarSocio_btn_limpiar)
        val db = DatabaseHelper(this)

        // Fecha por defecto en inscripción
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFechaInscripcion.setText(formatoFecha.format(Calendar.getInstance().time))

        // Date pickers
        etFechaNacimiento.setOnClickListener {
            showDatePicker(this, etFechaNacimiento, maxDate = System.currentTimeMillis())
        }
        etFechaInscripcion.setOnClickListener {
            showDatePicker(this, etFechaInscripcion)
        }

        // RegistroHelper
        val helper = RegistroHelper(this, db, "socio")
        // Botón aceptar
        btnAceptar.setOnClickListener {
            helper.registrar(etNombre, etApellido, etDni, etFechaNacimiento, etFechaInscripcion, cbAptoFisico)
        }

        // Botón Limpiar
        btnLimpiar.setOnClickListener {
            etNombre.text.clear()
            etApellido.text.clear()
            etDni.text.clear()
            etFechaNacimiento.text.clear()
        }

    }
}
