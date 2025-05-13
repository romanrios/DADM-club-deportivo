package com.grupo1dam.clubdeportivo

import com.grupo1dam.clubdeportivo.utils.setNavigationButton
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1dam.clubdeportivo.base.BaseActivity

class VencimientosActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)

        // Asegurar compatibilidad con los Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbarNavigation()

        setNavigationButton(R.id.vencimientos_btn_continuar, MenuActivity::class.java, "reverse")


        // 1. Referencia al ListView
        val listView = findViewById<ListView>(R.id.vencimientoslv)

        // 2. Datos de ejemplo
        val usuarios = listOf(
            "Juan Pérez",
            "María López",
            "Carlos Sánchez",
            "Laura Torres",
            "Ana Martínez"
        )

        // 3. Adaptador
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, usuarios)

        // 4. Asignar adaptador al ListView
        listView.adapter = adapter

    }
}
