package com.grupo1dam.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.grupo1dam.clubdeportivo.utils.DatabaseHelper

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // Forzar modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val databaseHelper = DatabaseHelper(this)

        val usuario = findViewById<EditText>(R.id.login_et_correo)
        val pass = findViewById<TextInputEditText>(R.id.login_et_contrasena)
        val continueButton = findViewById<Button>(R.id.login_btn_continuar)

        continueButton.setOnClickListener{
            val usuarioString = usuario.text.toString().trim()
            val passString = pass.text.toString().trim()

            if(databaseHelper.login(usuarioString, passString)){
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MenuActivity::class.java)
                usuario.text.clear()
                pass.text?.clear()

                startActivity(intent)

                } else {
                Toast.makeText(this, "Usuario incorrecto", Toast.LENGTH_SHORT).show()
            }
        }

    }

}