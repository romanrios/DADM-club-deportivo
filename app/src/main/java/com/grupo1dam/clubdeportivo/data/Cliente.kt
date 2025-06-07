package com.grupo1dam.clubdeportivo.data

data class Cliente(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: Int,
    val fechaNacimiento: String,
    val fechaInscripcion: String,
    val entregoAptoFisico: Int,
    val tipo: String
)