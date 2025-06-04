package com.grupo1dam.clubdeportivo.data

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
            CREATE TABLE cliente (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT,
            apellido TEXT,
            dni INTEGER UNIQUE,
            fechaNacimiento TEXT,
            fechaInscripcion TEXT,
            entregoAptoFisico INTEGER,
            tipo TEXT CHECK(tipo IN ('socio', 'noSocio')) NOT NULL
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

    fun insertarCliente(
        tipo: String, // "socio" o "noSocio"
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
            put("tipo", tipo)
        }
        val resultado = db.insert("cliente", null, valores)
        return resultado != -1L
    }

    fun existeDni(dni: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM cliente WHERE dni = ?", arrayOf(dni.toString()))
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    fun obtenerClientesPorTipo(tipo: String): List<Cliente> {
        val lista = mutableListOf<Cliente>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, nombre, apellido, fechaInscripcion FROM cliente WHERE tipo = ?",
            arrayOf(tipo)
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val apellido = cursor.getString(2)
                val fechaInscripcion = cursor.getString(3)
                lista.add(Cliente(id, nombre, apellido, fechaInscripcion))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }


}