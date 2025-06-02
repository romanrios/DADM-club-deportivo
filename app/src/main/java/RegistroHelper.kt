// RegistroHelper.kt
package com.grupo1dam.clubdeportivo.utils

import android.content.Context
import android.widget.*
import androidx.appcompat.app.AlertDialog

class RegistroHelper(
    private val context: Context,
    private val db: DatabaseHelper,
    private val tabla: String
) {
    fun registrar(
        etNombre: EditText,
        etApellido: EditText,
        etDni: EditText,
        etFechaNacimiento: EditText,
        etFechaInscripcion: EditText,
        cbAptoFisico: CheckBox
    ) {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val dniTexto = etDni.text.toString().trim()
        val fechaNacTexto = etFechaNacimiento.text.toString().trim()
        val fechaInsTexto = etFechaInscripcion.text.toString().trim()
        val aptoFisico = if (cbAptoFisico.isChecked) 1 else 0

        val campos = listOf(nombre, apellido, dniTexto, fechaNacTexto, fechaInsTexto)
        if (campos.any { it.isBlank() }) {
            Toast.makeText(context, "Por favor, completá todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaNacimiento = convertirFecha(fechaNacTexto)
        val fechaInscripcion = convertirFecha(fechaInsTexto)
        if (fechaNacimiento == null || fechaInscripcion == null) {
            Toast.makeText(context, "Las fechas ingresadas no son válidas", Toast.LENGTH_SHORT).show()
            return
        }

        val dni = dniTexto.toIntOrNull()
        if (dni == null) {
            Toast.makeText(context, "DNI inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (db.existeDniEnTabla(tabla, dni)) {
            Toast.makeText(context, "Ya existe un $tabla con ese DNI", Toast.LENGTH_SHORT).show()
            return
        }

        val resumen = """
            Nombre: $nombre
            Apellido: $apellido
            DNI: $dni
            Fecha de nacimiento: $fechaNacimiento
            Fecha de inscripción: $fechaInscripcion
            Apto físico: ${if (aptoFisico == 1) "Sí" else "No"}
        """.trimIndent()

        AlertDialog.Builder(context)
            .setTitle("Confirmar registro")
            .setMessage("¿Deseás guardar los siguientes datos?\n\n$resumen")
            .setPositiveButton("Sí") { _, _ ->
                val insertado = db.insertarPersona(
                    tabla, nombre, apellido, dni, fechaNacimiento, fechaInscripcion, aptoFisico
                )
                if (insertado) {
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    etNombre.text.clear()
                    etApellido.text.clear()
                    etDni.text.clear()
                    etFechaNacimiento.text.clear()
                } else {
                    Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
