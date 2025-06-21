package com.grupo1dam.clubdeportivo.ui.base

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.grupo1dam.clubdeportivo.R

open class BaseToolbarActivity : AppCompatActivity() {

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupToolbarNavigation()
    }

    fun setupToolbarNavigation() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar?.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}
