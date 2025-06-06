package com.grupo1dam.clubdeportivo.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1dam.clubdeportivo.R
import com.grupo1dam.clubdeportivo.ui.base.BaseActivity
import com.grupo1dam.clubdeportivo.data.DatabaseHelper

class VencimientosActivity : BaseActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_vencimientos)

        // Asegurar compatibilidad con los Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbarNavigation()

        findViewById<Button>(R.id.vencimientos_btn_continuar).setOnClickListener {
            finish()
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        dbHelper = DatabaseHelper(this)

        mostrarSocios()

    }

    // función mostrada en clase jueves 5/6/2025
    private fun mostrarSocios() {
        val listView = findViewById<ListView>(R.id.vencimientos_listview)
        val socios = dbHelper.obtenerClientesPorTipo("socio")

        // Convertir a un texto amigable para mostrar
        val listaTexto = socios.map { socio ->
            "ID: ${socio.id}\n${socio.nombre} ${socio.apellido}\nInscripción: ${socio.fechaInscripcion}"
        }

        // Adaptador simple para mostrar texto formateado
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaTexto)
        listView.adapter = adapter
    }
}
