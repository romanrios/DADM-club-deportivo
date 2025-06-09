package com.grupo1dam.clubdeportivo.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import com.grupo1dam.clubdeportivo.R
import com.grupo1dam.clubdeportivo.data.Cliente
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Constantes que definen las dimensiones y estilos básicos del PDF y del carnet
private const val PAGE_WIDTH = 300       // Ancho total de la página PDF
private const val PAGE_HEIGHT = 300      // Alto total de la página PDF
private const val CARD_WIDTH = 240       // Ancho del rectángulo que representa el carnet
private const val CARD_HEIGHT = 160      // Alto del rectángulo que representa el carnet
private const val CORNER_RADIUS = 12f    // Radio de los bordes redondeados del carnet
private const val SHADOW_OFFSET = 4f     // Desplazamiento para la sombra del carnet

// Función para generar PDF de carnet para un cliente
fun generarCarnetPdf(context: Context, cliente: Cliente) {
    val pdf = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
    val page = pdf.startPage(pageInfo)
    val canvas = page.canvas

    drawCarnet(canvas, context, cliente)   // Dibuja el carnet en el canvas
    pdf.finishPage(page)                   // Finaliza la página para que el PDF esté listo

    val fileName = generarNombreArchivo(cliente.id)  // Genera un nombre para el archivo PDF
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        guardarPdfAndroidQySuperior(context, pdf, fileName)  // Guarda con MediaStore en Android 10+
    } else {
        guardarPdfAndroid9(
            context, pdf, fileName
        )           // Guarda directamente en Descargas en Android <10
    }
}

// == FUNCIONES AUXILIARES ==
// Dibuja todos los elementos visuales del carnet en el canvas.
private fun drawCarnet(canvas: Canvas, context: Context, cliente: Cliente) {
    val azulOscuro = "#003366".toColorInt()
    val bebasNeue = ResourcesCompat.getFont(context, R.font.bebas_neue)

    val rectLeft = (PAGE_WIDTH - CARD_WIDTH) / 2f
    val rectTop = (PAGE_HEIGHT - CARD_HEIGHT) / 2f
    val rectRight = rectLeft + CARD_WIDTH
    val rectBottom = rectTop + CARD_HEIGHT
    val rect = RectF(rectLeft, rectTop, rectRight, rectBottom)

    val fondoPaint =
        Paint().apply { style = Paint.Style.FILL; color = Color.WHITE; isAntiAlias = true }
    val borderPaint = Paint().apply {
        style = Paint.Style.STROKE; strokeWidth = 1f; color = "#888888".toColorInt(); isAntiAlias =
        true
    }
    val shadowPaint = Paint().apply {
        style = Paint.Style.FILL; color = "#DDDDDD".toColorInt(); isAntiAlias = true
    }

    val shadowRect = RectF(
        rectLeft + SHADOW_OFFSET,
        rectTop + SHADOW_OFFSET,
        rectRight + SHADOW_OFFSET,
        rectBottom + SHADOW_OFFSET
    )
    canvas.drawRoundRect(shadowRect, CORNER_RADIUS, CORNER_RADIUS, shadowPaint)
    canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, fondoPaint)
    canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, borderPaint)

    val franjaHeight = 40f
    val franjaPath = Path().apply {
        moveTo(rectLeft, rectTop + franjaHeight)
        lineTo(rectLeft, rectTop + CORNER_RADIUS)
        quadTo(rectLeft, rectTop, rectLeft + CORNER_RADIUS, rectTop)
        lineTo(rectRight - CORNER_RADIUS, rectTop)
        quadTo(rectRight, rectTop, rectRight, rectTop + CORNER_RADIUS)
        lineTo(rectRight, rectTop + franjaHeight)
        close()
    }
    val franjaPaint =
        Paint().apply { style = Paint.Style.FILL; color = azulOscuro; isAntiAlias = true }
    canvas.drawPath(franjaPath, franjaPaint)

    val titleCarnet = Paint().apply {
        textSize = 18f
        isAntiAlias = true
        typeface = bebasNeue
        textAlign = Paint.Align.LEFT
        color = Color.WHITE
    }

    val titleClubAtletico = Paint().apply {
        textSize = 6f
        isAntiAlias = true
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.RIGHT
        color = Color.WHITE
    }

    val titleCincoEstrellas = Paint().apply {
        textSize = 10f
        isAntiAlias = true
        typeface = bebasNeue
        textAlign = Paint.Align.RIGHT
        color = Color.WHITE
    }

// "CARNET DE SOCIO" alineado a la izquierda
    canvas.drawText("CARNET DE SOCIO", rectLeft + 16f, rectTop + 29f, titleCarnet)

// CLUB ATLÉTICO / CINCO ESTRELLAS alineados a la derecha
    val rightTextX = rectRight - 16f
    canvas.drawText("CLUB ATLÉTICO", rightTextX, rectTop + 18f, titleClubAtletico)
    canvas.drawText("CINCO ESTRELLAS", rightTextX, rectTop + 29f, titleCincoEstrellas)


    val textoLabel = Paint().apply {
        textSize = 7f; isAntiAlias = true; typeface = Typeface.DEFAULT_BOLD; color = azulOscuro
    }
    val textoValor = Paint().apply {
        textSize = 9f; isAntiAlias = true; typeface = Typeface.DEFAULT; color = azulOscuro
    }

    var y = rectTop + franjaHeight + 24f
    val x = rectLeft + 16f
    val maxAnchoValor = PAGE_WIDTH - (x + 60f) - 30f

    fun drawCampo(label: String, value: String) {
        canvas.drawText("$label:", x, y, textoLabel)
        val valorFinal = ajustarTexto(value, maxAnchoValor, textoValor)
        canvas.drawText(valorFinal, x + 60f, y, textoValor)
        y += 16f
    }

    drawCampo("N° de Cliente", String.format(Locale.getDefault(), "%08d", cliente.id))
    drawCampo("DNI", cliente.dni.toString())
    drawCampo("Nombre", "${cliente.nombre} ${cliente.apellido}")
    drawCampo("Fecha de Nac.", cliente.fechaNacimiento)
    drawCampo("Fecha de Insc.", cliente.fechaInscripcion)
}

private fun ajustarTexto(text: String, maxWidth: Float, paint: Paint): String {
    var nuevoTexto = text
    while (paint.measureText(nuevoTexto) > maxWidth && nuevoTexto.isNotEmpty()) {
        nuevoTexto = nuevoTexto.dropLast(1)
    }
    return if (nuevoTexto != text && nuevoTexto.length > 1) nuevoTexto.dropLast(1) + "…" else nuevoTexto
}

// Crea un nombre de archivo único con la fecha y el ID del cliente.
private fun generarNombreArchivo(clienteId: Int): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
    val fecha = formatter.format(Date())
    return "carnet_${String.format("%08d", clienteId)}_$fecha.pdf"
}

// Guarda el PDF usando MediaStore para Android Q+.
private fun guardarPdfAndroidQySuperior(context: Context, pdf: PdfDocument, fileName: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        put(MediaStore.Downloads.IS_PENDING, 1)
    }

    val resolver = context.contentResolver

    @SuppressLint("NewApi") // Eliminamos advertencia porque ya lo tenemos esto bajo control
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null) {
        try {
            resolver.openOutputStream(uri)?.use { pdf.writeTo(it) }
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            Toast.makeText(context, "Carnet guardado en Descargas", Toast.LENGTH_LONG).show()

            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(openIntent)

        } catch (e: Exception) {
            Toast.makeText(context, "Error al guardar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdf.close()
        }
    } else {
        Toast.makeText(context, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show()
        pdf.close()
    }
}

// Guarda el PDF en el sistema de archivos tradicional para versiones anteriores.
private fun guardarPdfAndroid9(context: Context, pdf: PdfDocument, fileName: String) {
    val downloadsDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, fileName)

    try {
        FileOutputStream(file).use { pdf.writeTo(it) }
        Toast.makeText(context, "Carnet guardado en: ${file.absolutePath}", Toast.LENGTH_LONG)
            .show()

        val uri = Uri.fromFile(file)
        val openIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(openIntent)

    } catch (e: Exception) {
        Toast.makeText(context, "Error al guardar archivo: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdf.close()
    }
}


// ------------------------------------------------------- //

// Función para generar PDF de recibo de pago para un cliente
fun generarReciboPagoPdf(
    context: Context,
    cliente: Cliente,
    fechaPago: String,
    formaPago: String,
    monto: String,
    tipoCliente: String,
    tipoCuota: String
) {

    val pdf = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
    val page = pdf.startPage(pageInfo)
    val canvas = page.canvas

    drawRecibo(canvas, context, cliente, fechaPago, formaPago, monto, tipoCliente, tipoCuota)
    pdf.finishPage(page)

    val fileName = "recibo_pago_${cliente.dni}_${fechaPago.replace("-", "_")}.pdf"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        guardarPdfAndroidQySuperior(context, pdf, fileName)
    } else {
        guardarPdfAndroid9(context, pdf, fileName)
    }
}

private fun drawRecibo(
    canvas: Canvas,
    context: Context,
    cliente: Cliente,
    fechaPago: String,
    formaPago: String,
    monto: String,
    tipoCliente: String,
    tipoCuota: String
)
{
    val azulOscuro = "#003366".toColorInt()
    val bebasNeue = ResourcesCompat.getFont(context, R.font.bebas_neue)

    val centerX = PAGE_WIDTH / 2f
    var y = 40f

    // Título principal
    val paintTitulo = Paint().apply {
        isAntiAlias = true
        color = azulOscuro
        textSize = 20f
        typeface = bebasNeue
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("RECIBO DE PAGO", centerX, y, paintTitulo)

    y += 40f
    val labelX = 20f
    val valueX = 100f

    // Paint para los títulos (DNI:, Nombre:, etc.)
    val paintCampo = Paint().apply {
        isAntiAlias = true
        color = azulOscuro
        textSize = 7f
        typeface = Typeface.DEFAULT_BOLD
    }

    // Paint para los valores (datos del cliente)
    val paintValor = Paint().apply {
        isAntiAlias = true
        color = azulOscuro
        textSize = 11f
        typeface = Typeface.DEFAULT
    }

    fun drawFila(label: String, valor: String) {
        canvas.drawText(label, labelX, y, paintCampo)
        canvas.drawText(valor, valueX, y, paintValor)
        y += 20f
    }

    drawFila("Tipo de cliente:", tipoCliente)
    drawFila("Tipo de cuota:", tipoCuota)
    drawFila("Fecha de pago:", fechaPago)
    drawFila("DNI:", cliente.dni.toString())
    drawFila("Nombre:", "${cliente.nombre} ${cliente.apellido}")
    drawFila("Forma de pago:", formaPago)
    drawFila("Monto:", monto)



    // Nota al final
    val paintNota = Paint().apply {
        isAntiAlias = true
        color = azulOscuro
        textSize = 7f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }
    canvas.drawText(
        "Gracias por tu pago. Este recibo es válido como comprobante.", centerX, y + 15f, paintNota
    )

    // Pie institucional
    val paintPie1 = Paint().apply {
        isAntiAlias = true
        color = azulOscuro
        textSize = 8f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    val paintPie2 = Paint().apply {
        isAntiAlias = true
        color = azulOscuro
        textSize = 14f
        textAlign = Paint.Align.CENTER
        typeface = bebasNeue
    }

    val footerY1 = PAGE_HEIGHT - 30f
    val footerY2 = PAGE_HEIGHT - 15f
    canvas.drawText("CLUB ATLÉTICO", centerX, footerY1, paintPie1)
    canvas.drawText("CINCO ESTRELLAS", centerX, footerY2, paintPie2)
}


