package com.example.myapplication

import android.app.ActivityOptions
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

object ButtonConfigurator {
    fun <T> AppCompatActivity.configureButton(buttonId: Int, activityClass: Class<T>) {
        val button: Button = findViewById(buttonId)
        button.setOnClickListener {
            val intent = Intent(this, activityClass)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            startActivity(intent, options.toBundle())
        }
    }
}
