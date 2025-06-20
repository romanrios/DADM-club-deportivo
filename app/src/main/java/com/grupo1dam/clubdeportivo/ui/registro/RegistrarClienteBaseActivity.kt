package com.grupo1dam.clubdeportivo.ui.registro

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1dam.clubdeportivo.R
import com.grupo1dam.clubdeportivo.data.DatabaseHelper
import com.grupo1dam.clubdeportivo.ui.base.BaseActivity
import com.grupo1dam.clubdeportivo.utils.convertirFecha
import com.grupo1dam.clubdeportivo.utils.showDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Clase base abstracta para registrar clientes (socios o no socios)
abstract class RegistrarClienteBaseActivity : BaseActivity() {

    // Cada subclase define si trabaja con "socio" o "noSocio"
    protected abstract val tipoCliente: String

    // Elementos de UI del formulario
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etDni: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etFechaInscripcion: EditText
    private lateinit var cbAptoFisico: CheckBox
    private lateinit var btnAceptar: Button
    private lateinit var btnLimpiar: Button
    private val fechaDeHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)

    // Acceso a base de datos
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_cliente)

        // Título dinámico según el tipo de cliente (socio / no socio)
        val titulo = findViewById<TextView>(R.id.registrarCliente_txt_title)
        titulo.text = when (tipoCliente) {
            "socio" -> getString(R.string.registrarCliente_txt_registrarSocio)
            "noSocio" -> getString(R.string.registrarCliente_txt_registrarNoSocio)
            else -> getString(R.string.registrarCliente_txt_registrarCliente)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbarNavigation()
        db = DatabaseHelper(this) // Inicializa DB

        // Vincular componentes de UI con IDs del layout
        etNombre = findViewById(R.id.registrarCliente_et_nombre)
        etApellido = findViewById(R.id.registrarCliente_et_apellido)
        etDni = findViewById(R.id.registrarCliente_et_dni)
        etFechaNacimiento = findViewById(R.id.registrarCliente_et_fechaNacimiento)
        etFechaInscripcion = findViewById(R.id.registrarCliente_et_fechaInscripcion)
        cbAptoFisico = findViewById(R.id.registrarCliente_cb_aptoFisico)
        btnAceptar = findViewById(R.id.registrarCliente_btn_aceptar)
        btnLimpiar = findViewById(R.id.registrarCliente_btn_limpiar)

        // Fecha de inscripción por defecto: hoy
        etFechaInscripcion.setText(fechaDeHoy)

        // Mostrar DatePicker al hacer clic en los campos de fecha
        etFechaNacimiento.setOnClickListener {
            showDatePicker(this, etFechaNacimiento, maxDate = System.currentTimeMillis())
        }
        etFechaInscripcion.setOnClickListener {
            showDatePicker(this, etFechaInscripcion)
        }

        // Acciones de botones
        btnAceptar.setOnClickListener { registrarCliente() }
        btnLimpiar.setOnClickListener { limpiarCampos() }
    }

    // Limpia los campos del formulario
    private fun limpiarCampos() {
        etNombre.text.clear()
        etApellido.text.clear()
        etDni.text.clear()
        etFechaNacimiento.text.clear()
        etFechaInscripcion.setText(fechaDeHoy)
        cbAptoFisico.isChecked = false
    }

    // Recolecta datos, valida y guarda en la base
    private fun registrarCliente() {
        // Recolección de datos
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val dniTexto = etDni.text.toString().trim()
        val fechaNacTexto = etFechaNacimiento.text.toString().trim()
        val fechaInsTexto = etFechaInscripcion.text.toString().trim()
        val aptoFisico = if (cbAptoFisico.isChecked) 1 else 0

        // Validar que no haya campos vacíos
        val campos = listOf(nombre, apellido, dniTexto, fechaNacTexto, fechaInsTexto)
        if (campos.any { it.isBlank() }) {
            Toast.makeText(this, "Por favor, completá todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir fechas
        val fechaNacimiento = convertirFecha(fechaNacTexto)
        val fechaInscripcion = convertirFecha(fechaInsTexto)

        // Validar fechas
        if (fechaNacimiento == null || fechaInscripcion == null) {
            Toast.makeText(this, "Las fechas ingresadas no son válidas", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar DNI
        val dni = dniTexto.toIntOrNull()
        if (dni == null) {
            Toast.makeText(this, "DNI inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si ya existe ese DNI en la tabla correspondiente
        if (db.existeDni(dni)) {
            Toast.makeText(this, "Ya existe un cliente con ese DNI", Toast.LENGTH_SHORT).show()
            return
        }

        // Mostrar resumen y confirmar
        val resumen = """
            Nombre: $nombre
            Apellido: $apellido
            DNI: $dni
            Fecha de nacimiento: $fechaNacimiento
            Fecha de inscripción: $fechaInscripcion
            Apto físico: ${if (aptoFisico == 1) "Sí" else "No"}
        """.trimIndent()

        AlertDialog.Builder(this).setTitle("Confirmar registro")
            .setMessage("¿Deseás guardar los siguientes datos?\n\n$resumen")
            .setPositiveButton("Sí") { _, _ ->
                val insertado = db.insertarCliente(
                   nombre, apellido, dni, fechaNacimiento, fechaInscripcion, aptoFisico, tipoCliente
                )
                if (insertado) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }.setNegativeButton("Cancelar", null).show()
    }
}
