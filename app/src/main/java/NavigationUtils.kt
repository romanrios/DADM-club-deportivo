package com.grupo1dam.clubdeportivo.utils

import android.app.ActivityOptions
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

fun <T> AppCompatActivity.setNavigationButton(
    buttonId: Int,
    activityClass: Class<T>,
    transitionDirection: String? = null,
    shouldFinishCurrent: Boolean = true
) {
    val button: Button = findViewById(buttonId)
    button.setOnClickListener {
        val intent = Intent(this, activityClass)

        if (transitionDirection == "forward" || transitionDirection == "reverse") {
            val (enterAnim, exitAnim) = when (transitionDirection) {
                "reverse" -> com.grupo1dam.clubdeportivo.R.anim.slide_in_left to
                        com.grupo1dam.clubdeportivo.R.anim.slide_out_right
                else      -> com.grupo1dam.clubdeportivo.R.anim.slide_in_right to
                        com.grupo1dam.clubdeportivo.R.anim.slide_out_left
            }

            val options = ActivityOptions.makeCustomAnimation(this, enterAnim, exitAnim)
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }

        if (shouldFinishCurrent) {
            finish()
        }
    }
}