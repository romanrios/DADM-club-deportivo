package com.grupo1dam.clubdeportivo.ui

import com.grupo1dam.clubdeportivo.utils.setNavigationButton
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1dam.clubdeportivo.R

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setNavigationButton(R.id.menu_btn_registrarSocio, RegistrarSocioActivity::class.java, "forward",false)
        setNavigationButton(R.id.menu_btn_registrarNoSocio, RegistrarNoSocioActivity::class.java,"forward", false)
        setNavigationButton(R.id.menu_btn_vencimientos, VencimientosActivity::class.java,"forward", false)
        setNavigationButton(R.id.menu_btn_pagarcuota, PagarCuotaActivity::class.java,"forward", false)
        setNavigationButton(R.id.menu_btn_generarCarnet, CarnetActivity::class.java,"forward", false)

        findViewById<TextView>(R.id.menu_txt_cerrarsesion).setOnClickListener {
            finish()
        }

    }

}