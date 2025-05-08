package com.example.myapplication

import MyDatabaseHelper
import android.content.ContentValues
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DatabaseActivity : AppCompatActivity() {

    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var nameInput: EditText
    private lateinit var selectedName: String
    private var selectedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_database)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = MyDatabaseHelper(this)

        nameInput = findViewById(R.id.editTextName)
        val insertButton = findViewById<Button>(R.id.buttonInsert)
        val editButton = findViewById<Button>(R.id.buttonEdit)
        val deleteButton = findViewById<Button>(R.id.buttonDelete)
        listView = findViewById(R.id.listViewUsers)

        // Inicializar botones
        editButton.isEnabled = false
        deleteButton.isEnabled = false

        insertButton.setOnClickListener {
            val name = nameInput.text.toString()
            if (name.isNotEmpty()) {
                insertUser(name)
                nameInput.text.clear()
                Toast.makeText(this, "Usuario insertado", Toast.LENGTH_SHORT).show()
                loadUsers()
            }
        }

        // Cargar los usuarios cuando la actividad es creada
        loadUsers()

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedName = listView.adapter.getItem(position).toString()
            selectedPosition = position
            nameInput.setText(selectedName)

            // Habilitar botones si se selecciona un usuario
            editButton.isEnabled = true
            deleteButton.isEnabled = true
        }

        editButton.setOnClickListener {
            val newName = nameInput.text.toString()
            if (selectedPosition != -1 && newName.isNotEmpty()) {
                dbHelper.updateUser(selectedName, newName)
                Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show()
                nameInput.text.clear()
                selectedPosition = -1
                loadUsers()
            }
        }

        deleteButton.setOnClickListener {
            if (selectedPosition != -1) {
                dbHelper.deleteUser(selectedName)
                Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
                nameInput.text.clear()
                selectedPosition = -1
                loadUsers()
            }
        }
    }

    private fun insertUser(name: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(MyDatabaseHelper.COLUMN_NAME, name)
        }
        db.insert(MyDatabaseHelper.TABLE_NAME, null, values)
        db.close()
    }

    private fun loadUsers() {
        val users = dbHelper.getAllUsers()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)
        listView.adapter = adapter
    }
}
