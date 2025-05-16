package com.grupo1dam.clubdeportivo.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import java.util.Calendar
import java.util.Locale

fun showDatePicker(
    context: Context,
    targetEditText: EditText,
    onDateSelected: ((String) -> Unit)? = null,
    maxDate: Long? = null,
    minDate: Long? = null
) {
    val calendar = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = String.format(Locale.getDefault(),"%02d/%02d/%04d", dayOfMonth, month + 1, year)
            targetEditText.setText(selectedDate)
            targetEditText.setText(selectedDate)
            onDateSelected?.invoke(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    maxDate?.let { datePicker.datePicker.maxDate = it }
    minDate?.let { datePicker.datePicker.minDate = it }

    datePicker.show()
}
