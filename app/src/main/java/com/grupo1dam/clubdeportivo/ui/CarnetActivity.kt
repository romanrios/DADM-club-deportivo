package com.grupo1dam.clubdeportivo.ui

import android.text.Html
import android.view.View
import android.widget.Toast
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.grupo1dam.clubdeportivo.R
import com.grupo1dam.clubdeportivo.data.DatabaseHelper
import com.grupo1dam.clubdeportivo.ui.base.BaseToolbarActivity
import com.grupo1dam.clubdeportivo.utils.generarCarnetPdf

class CarnetActivity : BaseToolbarActivity() {

    private lateinit var etDni: TextInputEditText
    private lateinit var btnGenerar: Button
    private lateinit var btnDescargar: Button
    private lateinit var cardView: CardView
    private lateinit var txtInfo: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        etDni = findViewById(R.id.carnet_et_dni)
        btnGenerar = findViewById(R.id.carnet_btn_generar)
        btnDescargar = findViewById(R.id.carnet_btn_imprimir)
        cardView = findViewById(R.id.carnet_card_view)
        txtInfo = findViewById(R.id.carnet_txt_info)
        dbHelper = DatabaseHelper(this)

        btnGenerar.setOnClickListener {
            val dniStr = etDni.text.toString()
            if (dniStr.isBlank()) {
                Toast.makeText(this, "Completá el DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cliente = dbHelper.obtenerClientePorDni(dniStr.toInt())
            if (cliente == null) {
                Toast.makeText(this, "El cliente no existe", Toast.LENGTH_SHORT).show()
                cardView.visibility = View.GONE
                btnDescargar.visibility = View.GONE
                return@setOnClickListener
            }

            if (cliente.tipo != "socio") {
                Toast.makeText(this, "El cliente no es socio", Toast.LENGTH_SHORT).show()
                cardView.visibility = View.GONE
                btnDescargar.visibility = View.GONE
                return@setOnClickListener
            }

            val infoHtml = """
                <b>N° de cliente:</b> ${cliente.id.toString().padStart(8, '0')}<br>
                <b>DNI:</b> ${cliente.dni}<br>
                <b>Nombre:</b> ${cliente.nombre} ${cliente.apellido}<br>
                <b>Fecha de nacimiento:</b> ${cliente.fechaNacimiento}<br>
                <b>Fecha de inscripción:</b> ${cliente.fechaInscripcion}
            """.trimIndent()

            txtInfo.text = Html.fromHtml(infoHtml, Html.FROM_HTML_MODE_LEGACY)
            cardView.visibility = View.VISIBLE
            btnDescargar.visibility = View.VISIBLE
        }

        btnDescargar.setOnClickListener {
            val cliente = dbHelper.obtenerClientePorDni(etDni.text.toString().toInt())
            if (cliente != null) {
                generarCarnetPdf(this, cliente)
            } else {
                Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
