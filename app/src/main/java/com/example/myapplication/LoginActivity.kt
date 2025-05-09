package com.example.myapplication

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configureButton(R.id.btn_registrarSocio, MenuActivity::class.java)

    }

    // función para simplificar declaración de cada botón
    private fun <T> configureButton(buttonId: Int, activityClass: Class<T>) {
        val button: Button = findViewById(buttonId)
        button.setOnClickListener {
            val intent = Intent(this, activityClass)
            // animación
            val options = ActivityOptions.makeCustomAnimation(
                this, R.anim.slide_in_right, R.anim.slide_out_left
            )
            startActivity(intent, options.toBundle())
        }
    }


}