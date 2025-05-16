package com.grupo1dam.clubdeportivo

import com.grupo1dam.clubdeportivo.utils.setNavigationButton
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1dam.clubdeportivo.base.BaseActivity
import com.grupo1dam.clubdeportivo.utils.showDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistrarNoSocioActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_no_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbarNavigation()

        setNavigationButton(R.id.registrarnosocio_btn_aceptar, OperacionExitosaActivity::class.java)

        // Lógica para los campos de fecha
        val etFechaNacimiento = findViewById<EditText>(R.id.et_fecha_nacimiento)
        val etFechaInscripcion = findViewById<EditText>(R.id.et_fecha_inscripcion)

        // Establecer fecha actual como valor por defecto
        val fechaActual = Calendar.getInstance().time
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFechaInscripcion.setText(formato.format(fechaActual))

        etFechaNacimiento.setOnClickListener {
            showDatePicker(this, etFechaNacimiento, maxDate = System.currentTimeMillis())
        }

        etFechaInscripcion.setOnClickListener {
            showDatePicker(this, etFechaInscripcion)
        }
    }
}