package com.grupo1dam.clubdeportivo.ui

import android.os.Bundle
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.grupo1dam.clubdeportivo.R
import com.grupo1dam.clubdeportivo.data.DatabaseHelper
import com.grupo1dam.clubdeportivo.ui.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

class PagarCuotaActivity : BaseActivity() {

    private lateinit var etDni: TextInputEditText
    private lateinit var btnPagar: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_cuota)

        setupToolbarNavigation()
        dbHelper = DatabaseHelper(this)

        etDni = findViewById(R.id.et_pagarcuota_dni)
        btnPagar = findViewById(R.id.pagarcuota_btn_pagar)
        radioGroup = findViewById(R.id.radioGroup)

        btnPagar.setOnClickListener { pagarCuota() }
    }

    private fun pagarCuota() {
        val dniStr = etDni.text?.toString()
        if (dniStr.isNullOrBlank()) {
            Toast.makeText(this, "Ingresá un DNI", Toast.LENGTH_SHORT).show()
            return
        }

        val dni = dniStr.toIntOrNull()
        if (dni == null) {
            Toast.makeText(this, "DNI inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = dbHelper.obtenerClientePorDni(dni)
        if (cliente == null) {
            Toast.makeText(this, "El cliente no existe", Toast.LENGTH_SHORT).show()
            return
        }

        val formaPago = when (radioGroup.checkedRadioButtonId) {
            R.id.radioButton -> "Efectivo"
            R.id.radioButton2 -> "Tarjeta"
            else -> {
                Toast.makeText(this, "Seleccioná una forma de pago", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val hoy = Date()
        val sdfFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaPago = sdfFecha.format(hoy)

        val yaPago = if (cliente.tipo == "socio") {
            dbHelper.socioYaPagoEsteMes(cliente.id)
        } else {
            dbHelper.noSocioYaPagoHoy(cliente.id)
        }

        if (yaPago) {
            val msg = if (cliente.tipo == "socio") {
                "La cuota mensual del socio ya está abonada."
            } else {
                "El no socio ya abonó la cuota diaria de hoy."
            }
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            return
        }

        val fechaVencimiento = if (cliente.tipo == "socio") {
            val cal = Calendar.getInstance()
            cal.time = hoy
            cal.add(Calendar.DAY_OF_YEAR, 30)
            sdfFecha.format(cal.time)
        } else {
            fechaPago // vence hoy mismo o mañana si preferís
        }

        val resultado = dbHelper.registrarCuota(
            cliente.id,
            nroCuota = 1, // si querés podés calcularla
            formaPago = formaPago,
            fechaPago = fechaPago,
            fechaVencimiento = fechaVencimiento
        )

        if (resultado) {
            Toast.makeText(this, "Pago registrado con éxito", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_LONG).show()
        }
    }
}
