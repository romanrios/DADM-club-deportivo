package com.grupo1dam.clubdeportivo.utils

import android.app.ActivityOptions
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

fun <T> AppCompatActivity.configureButton(buttonId: Int, activityClass: Class<T>) {
    val button: Button = findViewById(buttonId)
    button.setOnClickListener {
        val intent = Intent(this, activityClass)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            com.grupo1dam.clubdeportivo.R.anim.slide_in_right,
            com.grupo1dam.clubdeportivo.R.anim.slide_out_left
        )
        startActivity(intent, options.toBundle())
    }
}
