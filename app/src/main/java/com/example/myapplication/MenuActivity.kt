package com.example.myapplication

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        configureButton(R.id.btn_registrarSocio, RegistrarSocioActivity::class.java)
        configureButton(R.id.btn_registrarNoSocio, RegistrarNoSocioActivity::class.java)
        configureButton(R.id.btn_listarVencimientos, VencimientosActivity::class.java)
        configureButton(R.id.btn_pagarCuota, PagarCuotaActivity::class.java)
        configureButton(R.id.btn_generarCarnet, GenerarCarnetActivity::class.java)

    }

    // función para simplificar declaración de cada botón
    private fun <T> configureButton(buttonId: Int, activityClass: Class<T>) {
        val button: Button = findViewById(buttonId)
        button.setOnClickListener {
            val intent = Intent(this, activityClass)
            // animación
            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            startActivity(intent, options.toBundle())
        }
    }
}