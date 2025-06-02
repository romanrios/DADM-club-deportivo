package com.grupo1dam.clubdeportivo

import com.grupo1dam.clubdeportivo.utils.showDatePicker
import com.grupo1dam.clubdeportivo.utils.convertirFecha

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1dam.clubdeportivo.base.BaseActivity
import com.grupo1dam.clubdeportivo.utils.DatabaseHelper
import com.grupo1dam.clubdeportivo.utils.setNavigationButton
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

        setNavigationButton(R.id.registrarSocio_btn_aceptar, OperacionExitosaActivity::class.java)

        // Asignaciones
        val etNombre = findViewById<EditText>(R.id.registrarSocio_et_nombre)
        val etApellido = findViewById<EditText>(R.id.registrarSocio_et_apellido)
        val etDni = findViewById<EditText>(R.id.registrarSocio_et_dni)
        val etFechaNacimiento = findViewById<EditText>(R.id.registrarSocio_et_fechaNacimiento)
        val etFechaInscripcion = findViewById<EditText>(R.id.registrarSocio_et_fechaInscripcion)
        val cbAptoFisico = findViewById<CheckBox>(R.id.registrarSocio_cb_aptoFisico)
        val btnAceptar = findViewById<Button>(R.id.registrarSocio_btn_aceptar)
        val btnLimpiar = findViewById<Button>(R.id.registrarSocio_btn_limpiar)
        val databaseHelper = DatabaseHelper(this)

        // Establecer fecha actual como valor por defecto
        val fechaActual = Calendar.getInstance().time
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFechaInscripcion.setText(formato.format(fechaActual))

        // Mostrar selector de fecha al hacer clic en los campos
        etFechaNacimiento.setOnClickListener {
            showDatePicker(this, etFechaNacimiento, maxDate = System.currentTimeMillis())
        }
        etFechaInscripcion.setOnClickListener {
            showDatePicker(this, etFechaInscripcion)
        }

        // Botón aceptar
        btnAceptar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dniTexto = etDni.text.toString().trim()
            val fechaNacimiento = convertirFecha(etFechaNacimiento.text.toString()).toString()
            val fechaInscripcion = convertirFecha(etFechaInscripcion.text.toString()).toString()
            val aptoFisico = if (cbAptoFisico.isChecked) 1 else 0

            // Validaciones
            val campos = listOf(nombre, apellido, dniTexto, fechaNacimiento, fechaInscripcion)
            if (campos.any { it.isEmpty() }) {
                Toast.makeText(this, "Por favor, completá todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dni = dniTexto.toIntOrNull()
            if (dni == null) {
                Toast.makeText(this, "DNI inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lógica para registrar socio
            if (databaseHelper.insertarSocio(
                    nombre, apellido, dni, fechaNacimiento, fechaInscripcion, aptoFisico
                )
            ) {
                Toast.makeText(this, "Socio agregado", Toast.LENGTH_SHORT).show()
                etNombre.text.clear()
                etApellido.text.clear()
                etDni.text.clear()
                etFechaNacimiento.text.clear()
            }
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
