package com.grupo1dam.clubdeportivo

import com.grupo1dam.clubdeportivo.utils.configureButton
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        configureButton(R.id.btn_continuar, RegistrarSocioActivity::class.java)
        configureButton(R.id.btn_registrarNoSocio, RegistrarNoSocioActivity::class.java)
        configureButton(R.id.btn_listarVencimientos, VencimientosActivity::class.java)
        configureButton(R.id.btn_pagarCuota, PagarCuotaActivity::class.java)
        configureButton(R.id.btn_generarCarnet, GenerarCarnetActivity::class.java)

    }


}