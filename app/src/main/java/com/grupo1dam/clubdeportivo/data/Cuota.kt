package com.grupo1dam.clubdeportivo.data

data class Cuota(
    val id: Int = 0,
    val idCliente: Int,
    val nroCuota: Int,
    val formaPago: String,
    val fechaPago: String,
    val fechaVencimiento: String,
    val tipo: String,
    val monto: Double
)
