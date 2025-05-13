package com.grupo1dam.clubdeportivo.base

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.grupo1dam.clubdeportivo.MenuActivity
import com.grupo1dam.clubdeportivo.R

open class BaseActivity : AppCompatActivity() {

    fun setupToolbarNavigation() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar?.setNavigationOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)

            val enterAnim = R.anim.slide_in_left
            val exitAnim = R.anim.slide_out_right
            val options = ActivityOptions.makeCustomAnimation(this, enterAnim, exitAnim)

            startActivity(intent, options.toBundle())
        }
    }
}
