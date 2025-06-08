package com.grupo1dam.clubdeportivo.ui

import android.text.Html
import android.view.View
import android.widget.Toast
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.grupo1dam.clubdeportivo.R
import com.grupo1dam.clubdeportivo.data.DatabaseHelper
import com.grupo1dam.clubdeportivo.ui.base.BaseActivity

import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

import androidx.core.graphics.toColorInt
import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import android.net.Uri
import android.os.Build
import androidx.core.content.res.ResourcesCompat

class CarnetActivity : BaseActivity() {

    private lateinit var etDni: TextInputEditText
    private lateinit var btnGenerar: Button
    private lateinit var btnImprimir: Button
    private lateinit var cardView: CardView
    private lateinit var txtInfo: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        setupToolbarNavigation()

        etDni = findViewById(R.id.carnet_et_dni)
        btnGenerar = findViewById(R.id.carnet_btn_generar)
        btnImprimir = findViewById(R.id.carnet_btn_imprimir)
        cardView = findViewById(R.id.carnet_card_view)
        txtInfo = findViewById(R.id.carnet_txt_info)
        dbHelper = DatabaseHelper(this)

        btnGenerar.setOnClickListener {
            val dniStr = etDni.text.toString()
            if (dniStr.isBlank()) {
                Toast.makeText(this, "Completá el DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cliente = dbHelper.obtenerClientePorDni(dniStr.toInt())
            if (cliente == null) {
                Toast.makeText(this, "El cliente no existe", Toast.LENGTH_SHORT).show()
                cardView.visibility = View.GONE
                btnImprimir.visibility = View.GONE
                return@setOnClickListener
            }

            if (cliente.tipo != "socio") {
                Toast.makeText(this, "El cliente no es socio", Toast.LENGTH_SHORT).show()
                cardView.visibility = View.GONE
                btnImprimir.visibility = View.GONE
                return@setOnClickListener
            }

            val infoHtml = """
                <b>ID:</b> ${cliente.id}<br>
                <b>DNI:</b> ${cliente.dni}<br>
                <b>Nombre:</b> ${cliente.nombre} ${cliente.apellido}<br>
                <b>Fecha de nacimiento:</b> ${cliente.fechaNacimiento}<br>
                <b>Fecha de inscripción:</b> ${cliente.fechaInscripcion}
            """.trimIndent()

            txtInfo.text = Html.fromHtml(infoHtml, Html.FROM_HTML_MODE_LEGACY)
            cardView.visibility = View.VISIBLE
            btnImprimir.visibility = View.VISIBLE
        }

        btnImprimir.setOnClickListener {
            val cliente = dbHelper.obtenerClientePorDni(etDni.text.toString().toInt())

            if (cliente == null) {
                Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pdf = PdfDocument()
            val pageWidth = 300
            val pageHeight = 400
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdf.startPage(pageInfo)

            val canvas = page.canvas
            val azulOscuro = "#003366".toColorInt()
            val bebasNeue = ResourcesCompat.getFont(this, R.font.bebas_neue)

            val paintTitulo = Paint().apply {
                textSize = 18f
                isAntiAlias = true
                typeface = Typeface.DEFAULT
                textAlign = Paint.Align.CENTER
                color = azulOscuro
            }

            val paintSubtitulo = Paint().apply {
                textSize = 22f
                isAntiAlias = true
                typeface = bebasNeue
                textAlign = Paint.Align.CENTER
                color = azulOscuro
            }

            val textoPaint = Paint().apply {
                textSize = 12f
                isAntiAlias = true
                typeface = Typeface.DEFAULT
                color = azulOscuro
            }

            val borderPaint = Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 1.2f
                color = "#888888".toColorInt()
                isAntiAlias = true
            }

            val fondoPaint = Paint().apply {
                style = Paint.Style.FILL
                color = android.graphics.Color.WHITE
                isAntiAlias = true
            }

            val shadowPaint = Paint().apply {
                style = Paint.Style.FILL
                color = "#CCCCCC".toColorInt()
                isAntiAlias = true
            }

            val cornerRadius = 20f
            val rectWidth = 240
            val rectHeight = 180
            val rectLeft = (pageWidth - rectWidth) / 2f
            val rectTop = (pageHeight - rectHeight) / 2f
            val rectRight = rectLeft + rectWidth
            val rectBottom = rectTop + rectHeight
            val rect = android.graphics.RectF(rectLeft, rectTop, rectRight, rectBottom)

            val shadowOffset = 4f
            val shadowRect = android.graphics.RectF(
                rectLeft + shadowOffset,
                rectTop + shadowOffset,
                rectRight + shadowOffset,
                rectBottom + shadowOffset
            )
            canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint)
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, fondoPaint)
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

            val tituloY = rectTop - 40
            val subtituloY = rectTop - 15
            canvas.drawText("CLUB ATLÉTICO", pageWidth / 2f, tituloY, paintTitulo)
            canvas.drawText("CINCO ESTRELLAS", pageWidth / 2f, subtituloY, paintSubtitulo)

            var y = rectTop + 30
            val x = rectLeft + 16

            fun drawLine(label: String, value: String) {
                canvas.drawText("$label: $value", x, y, textoPaint)
                y += 20
            }

            drawLine("ID", cliente.id.toString())
            drawLine("DNI", cliente.dni.toString())
            drawLine("Nombre", "${cliente.nombre} ${cliente.apellido}")
            drawLine("Fecha nacimiento", cliente.fechaNacimiento)
            drawLine("Fecha inscripción", cliente.fechaInscripcion)

            pdf.finishPage(page)

            val fileName = "Carnet_${cliente.dni}.pdf"

            // Guardar según versión de Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ → MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri != null) {
                    try {
                        resolver.openOutputStream(uri)?.use { outputStream ->
                            pdf.writeTo(outputStream)
                        }

                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)

                        Toast.makeText(this, "Carnet guardado en Descargas", Toast.LENGTH_LONG).show()

                        val openIntent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        startActivity(openIntent)

                    } catch (e: Exception) {
                        Toast.makeText(this, "Error al guardar PDF: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        pdf.close()
                    }
                } else {
                    Toast.makeText(this, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show()
                    pdf.close()
                }

            } else {
                // Android 9 o inferior → File directamente en /Download
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                try {
                    FileOutputStream(file).use { outputStream ->
                        pdf.writeTo(outputStream)
                    }

                    Toast.makeText(this, "Carnet guardado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()

                    val uri = Uri.fromFile(file)
                    val openIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    startActivity(openIntent)

                } catch (e: Exception) {
                    Toast.makeText(this, "Error al guardar archivo: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    pdf.close()
                }
            }
        }




    }
}
