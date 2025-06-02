package com.grupo1dam.clubdeportivo.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "clubDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE usuario(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT UNIQUE,
            pass TEXT
        )""".trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE socio(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT,
            apellido TEXT,
            dni INTEGER UNIQUE,
            fechaNacimiento TEXT, 
            fechaInscripcion TEXT,
            entregoAptoFisico INTEGER 
            )""".trimIndent()
        )

        db.execSQL("INSERT INTO usuario (nombre, pass) values ('admin', '1234')")
        db.execSQL("INSERT INTO usuario (nombre, pass) values ('admin2', '12345678')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun login(nombre: String, pass: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuario WHERE nombre=? AND pass=?", arrayOf(nombre, pass)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun insertarSocio(
        nombre: String,
        apellido: String,
        dni: Int,
        fechaNacimiento: String,
        fechaInscripcion: String,
        entregoAptoFisico: Int
    ): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("fechaNacimiento", fechaNacimiento)
            put("fechaInscripcion", fechaInscripcion)
            put("entregoAptoFisico", entregoAptoFisico)
        }
        val resultado = db.insert("socio", null, valores)
        return resultado != -1L
    }


}