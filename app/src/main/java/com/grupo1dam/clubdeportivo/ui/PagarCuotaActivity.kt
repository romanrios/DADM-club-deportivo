package com.grupo1dam.clubdeportivo.ui

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.grupo1dam.clubdeportivo.R
import com.grupo1dam.clubdeportivo.data.Cliente
import com.grupo1dam.clubdeportivo.data.Cuota
import com.grupo1dam.clubdeportivo.data.DatabaseHelper
import com.grupo1dam.clubdeportivo.ui.base.BaseActivity
import com.grupo1dam.clubdeportivo.utils.generarReciboPagoPdf
import java.text.SimpleDateFormat
import java.util.*

class PagarCuotaActivity : BaseActivity() {

    private lateinit var etDni: TextInputEditText
    private lateinit var btnPagar: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var reciboContainer: View
    private lateinit var txtReciboContenido: TextView
    private lateinit var btnGenerarPdf: Button
    private lateinit var dbHelper: DatabaseHelper
    private var cuotaActual: Cuota? = null
    private var clienteActual: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_cuota)

        setupToolbarNavigation()
        dbHelper = DatabaseHelper(this)

        etDni = findViewById(R.id.et_pagarcuota_dni)
        btnPagar = findViewById(R.id.pagarcuota_btn_pagar)
        radioGroup = findViewById(R.id.radioGroup)
        reciboContainer = findViewById(R.id.recibo_container)
        txtReciboContenido = findViewById(R.id.recibo_txt_contenido)
        btnGenerarPdf = findViewById(R.id.pagarCuota_btn_generarPdf)

        btnPagar.setOnClickListener { pagarCuota() }

        btnGenerarPdf.setOnClickListener {
            val cliente = clienteActual
            val cuota = cuotaActual
            if (cliente != null && cuota != null) {
                generarReciboPagoPdf(
                    context = this,
                    cliente = cliente,
                    fechaPago = cuota.fechaPago,
                    formaPago = cuota.formaPago,
                    monto = "$${String.format("%.2f", cuota.monto)}",
                    tipoCliente = if (cliente.tipo == "socio") "Socio" else "No socio",
                    tipoCuota = cuota.tipo.capitalize()
                )

            } else {
                Toast.makeText(this, "Faltan datos para generar el recibo", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun pagarCuota() {
        val dniStr = etDni.text?.toString()
        val dni = dniStr?.toIntOrNull()
        if (dniStr.isNullOrBlank() || dni == null) {
            Toast.makeText(this, "DNI inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = dbHelper.obtenerClientePorDni(dni) ?: run {
            Toast.makeText(this, "El cliente no existe", Toast.LENGTH_SHORT).show()
            return
        }

        val formaPago = when (radioGroup.checkedRadioButtonId) {
            R.id.carnet_rb_efectivo -> "Efectivo"
            R.id.carnet_rb_tarjeta -> "Tarjeta"
            else -> {
                Toast.makeText(this, "Seleccioná una forma de pago", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val hoy = sdf.format(Date())
        val esSocio = cliente.tipo == "socio"
        val yaPago =
            if (esSocio) dbHelper.socioYaPagoEsteMes(cliente.id) else dbHelper.noSocioYaPagoHoy(
                cliente.id
            )

        val tipoCuota = if (esSocio) "mensual" else "diaria"
        val precioBase = if (esSocio) 12000 else 500
        val montoFinal = if (formaPago == "Tarjeta") precioBase * 1.1 else precioBase.toDouble()
        val montoFormateado = "$${String.format("%.2f", montoFinal)}"
        val nroCuota = if (esSocio) dbHelper.obtenerProximoNroCuota(cliente.id) else 1
        val fechaVencimiento = if (esSocio) {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 30) }
            sdf.format(cal.time)
        } else {
            hoy
        }

        val cuota = Cuota(
            idCliente = cliente.id,
            nroCuota = nroCuota,
            formaPago = formaPago,
            fechaPago = hoy,
            fechaVencimiento = fechaVencimiento,
            tipo = tipoCuota,
            monto = montoFinal
        )

        if (yaPago) {
            AlertDialog.Builder(this).setTitle("Cuota ya abonada")
                .setMessage("Este cliente ya abonó la cuota. ¿Deseás reimprimir el recibo?")
                .setPositiveButton("Sí") { _, _ ->
                    val ultimaCuota = dbHelper.obtenerUltimaCuota(cliente.id, tipoCuota)
                    if (ultimaCuota != null) {
                        clienteActual = cliente
                        cuotaActual = ultimaCuota
                        mostrarRecibo(cliente, ultimaCuota, mostrarPdf = true)
                    } else {
                        Toast.makeText(this, "No se encontró el pago anterior", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.setNegativeButton("No", null).show()
            return
        }

        AlertDialog.Builder(this).setTitle("Confirmar pago").setMessage(
            """
            Cliente: ${cliente.nombre} ${cliente.apellido}
            Tipo: ${if (esSocio) "Socio" else "No socio"}
            Cuota: ${tipoCuota.capitalize()}
            Forma de pago: $formaPago
            Monto: $montoFormateado

            ¿Confirmás el registro del pago?
             """.trimIndent()
        ).setPositiveButton("Confirmar") { _, _ ->
            val exito = dbHelper.registrarCuota(
                idCliente = cuota.idCliente,
                nroCuota = cuota.nroCuota,
                formaPago = cuota.formaPago,
                fechaPago = cuota.fechaPago,
                fechaVencimiento = cuota.fechaVencimiento,
                tipo = cuota.tipo,
                monto = cuota.monto
            )

            if (exito) {
                clienteActual = cliente
                cuotaActual = cuota
                Toast.makeText(this, "Pago registrado con éxito", Toast.LENGTH_LONG).show()
                mostrarRecibo(cliente, cuota, mostrarPdf = true)
            } else {
                Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_LONG).show()
            }
        }.setNegativeButton("Cancelar", null).show()
    }

    private fun mostrarRecibo(cliente: Cliente, cuota: Cuota, mostrarPdf: Boolean) {
        val esSocio = cliente.tipo == "socio"
        val montoFormateado = "$${String.format("%.2f", cuota.monto)}"

        val lineas = mutableListOf<String>()
        fun linea(titulo: String, valor: String) {
            lineas.add("<b style='color:#003366;'>$titulo:</b> <span style='font-size:18px;'>$valor</span>")
        }

        linea("Tipo de cliente", if (esSocio) "Socio" else "No socio")
        linea("Tipo de cuota", cuota.tipo.capitalize())
        linea("Fecha", cuota.fechaPago)
        linea("DNI", cliente.dni.toString())
        linea("Forma de pago", cuota.formaPago)
        linea("Monto", montoFormateado)

        if (esSocio) {
            linea("N° Cuota", cuota.nroCuota.toString())
            linea("Vencimiento", cuota.fechaVencimiento)
        }

        txtReciboContenido.text = Html.fromHtml(
            lineas.joinToString("<br>"), Html.FROM_HTML_MODE_LEGACY
        )

        reciboContainer.visibility = View.VISIBLE
        btnGenerarPdf.visibility = if (mostrarPdf) View.VISIBLE else View.GONE
    }
}
