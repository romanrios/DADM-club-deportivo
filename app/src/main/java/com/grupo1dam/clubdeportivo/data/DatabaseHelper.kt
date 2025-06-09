package com.grupo1dam.clubdeportivo.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "clubDB", null, 1) {

    // Habilitar foreign keys
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

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

        db.execSQL(
            """
            CREATE TABLE cuota (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            idCliente INTEGER,
            nroCuota INTEGER,
            formaPago TEXT,
            fechaPago TEXT,
            fechaVencimiento TEXT,
            tipo TEXT,
            monto REAL,
            FOREIGN KEY(idCliente) REFERENCES cliente(id) ON DELETE CASCADE
        )""".trimIndent()
        )
        // ON DELETE CASCADE: si se borra un cliente también se borren sus cuotas

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
        nombre: String,
        apellido: String,
        dni: Int,
        fechaNacimiento: String,
        fechaInscripcion: String,
        entregoAptoFisico: Int,
        tipo: String
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

    // Función mostrada en clase del jueves 5/6/2025
    fun obtenerClientesPorTipo(tipo: String): List<Cliente> {
        val lista = mutableListOf<Cliente>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM cliente WHERE tipo = ?",
            arrayOf(tipo) // si no hubiera argumentos este segundo param puede ser null
        )

        if (cursor.moveToFirst()) {
            do {
                val cliente = Cliente(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    apellido = cursor.getString(2),
                    dni = cursor.getInt(3),
                    fechaNacimiento = cursor.getString(4),
                    fechaInscripcion = cursor.getString(5),
                    entregoAptoFisico = cursor.getInt(6),
                    tipo = cursor.getString(7)
                )
                lista.add(cliente)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }


    fun obtenerSociosConCuotaVencidaHoy(): List<Cliente> {
        val socios = mutableListOf<Cliente>()
        val db = readableDatabase

        val fechaHoy =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        val query = """
        SELECT c.*
        FROM cliente c
        INNER JOIN cuota q ON c.id = q.idCliente
        WHERE c.tipo = 'socio' AND q.fechaVencimiento = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(fechaHoy))

        if (cursor.moveToFirst()) {
            do {
                val cliente = Cliente(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                    dni = cursor.getInt(cursor.getColumnIndexOrThrow("dni")),
                    fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaNacimiento")),
                    fechaInscripcion = cursor.getString(cursor.getColumnIndexOrThrow("fechaInscripcion")),
                    entregoAptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("entregoAptoFisico")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                )
                socios.add(cliente)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return socios
    }


    // PARA PAGAR CUOTA
    fun obtenerClientePorDni(dni: Int): Cliente? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM cliente WHERE dni = ?", arrayOf(dni.toString()))
        var cliente: Cliente? = null
        if (cursor.moveToFirst()) {
            cliente = Cliente(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                apellido = cursor.getString(2),
                dni = cursor.getInt(3),
                fechaNacimiento = cursor.getString(4),
                fechaInscripcion = cursor.getString(5),
                entregoAptoFisico = cursor.getInt(6),
                tipo = cursor.getString(7)
            )
        }
        cursor.close()
        return cliente
    }

    fun noSocioYaPagoHoy(clienteId: Int): Boolean {
        val db = readableDatabase
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cursor = db.rawQuery(
            "SELECT 1 FROM cuota WHERE idCliente = ? AND fechaPago = ?",
            arrayOf(clienteId.toString(), hoy)
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    fun socioYaPagoEsteMes(clienteId: Int): Boolean {
        val db = readableDatabase
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val mesActual = sdf.format(Date()) + "%"
        val cursor = db.rawQuery(
            "SELECT 1 FROM cuota WHERE idCliente = ? AND fechaPago LIKE ?",
            arrayOf(clienteId.toString(), mesActual)
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    fun registrarCuota(
        idCliente: Int,
        nroCuota: Int,
        formaPago: String,
        fechaPago: String,
        fechaVencimiento: String,
        tipo: String,
        monto: Double
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idCliente", idCliente)
            put("nroCuota", nroCuota)
            put("formaPago", formaPago)
            put("fechaPago", fechaPago)
            put("fechaVencimiento", fechaVencimiento)
            put("tipo", tipo)
            put("monto", monto)
        }

        val resultado = db.insert("cuota", null, values)
        return resultado != -1L
    }

    fun obtenerProximoNroCuota(clienteId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT MAX(nroCuota) FROM cuota WHERE idCliente = ?", arrayOf(clienteId.toString())
        )
        val proximo = if (cursor.moveToFirst()) {
            cursor.getInt(0) + 1
        } else {
            1
        }
        cursor.close()
        return if (proximo == 0) 1 else proximo
    }

    fun obtenerUltimaCuota(idCliente: Int, tipo: String): Cuota? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT * FROM cuota 
        WHERE idCliente = ? AND tipo = ? 
        ORDER BY fechaPago DESC 
        LIMIT 1
        """.trimIndent(), arrayOf(idCliente.toString(), tipo)
        )

        return if (cursor.moveToFirst()) {
            Cuota(
                idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("idCliente")),
                nroCuota = cursor.getInt(cursor.getColumnIndexOrThrow("nroCuota")),
                formaPago = cursor.getString(cursor.getColumnIndexOrThrow("formaPago")),
                fechaPago = cursor.getString(cursor.getColumnIndexOrThrow("fechaPago")),
                fechaVencimiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaVencimiento")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto"))
            )
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }


}

