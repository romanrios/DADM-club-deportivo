package com.grupo1dam.clubdeportivo.ui.base

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.grupo1dam.clubdeportivo.R

open class BaseActivity : AppCompatActivity() {

    fun setupToolbarNavigation() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar?.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}
