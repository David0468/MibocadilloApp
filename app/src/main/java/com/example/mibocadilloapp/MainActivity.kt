package com.example.mibocadilloapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mibocadilloapp.ui.admin.AdminNavManager
import com.example.mibocadilloapp.ui.alumno.BottomNavManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        verificarRolUsuario(user)
                    }
                } else {
                    Toast.makeText(this, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun verificarRolUsuario(user: FirebaseUser) {
        val db = Firebase.firestore
        val email = user.email ?: return

        // 🔹 Buscar en la colección unificada "usuarios"
        db.collection("usuarios").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val tipoUsuario = document.getString("tipo_usuario")

                    when (tipoUsuario) {
                        "alumno" -> {
                            Toast.makeText(this, "Bienvenido, Alumno", Toast.LENGTH_SHORT).show()
                            irASeleccionBocadillo()
                        }
                        "cocina" -> {
                            Toast.makeText(this, "Bienvenido, Cocina", Toast.LENGTH_SHORT).show()
                            irASeleccionBocadillo()
                        }
                        "admin" -> {
                            Toast.makeText(this, "Bienvenido, Administrador", Toast.LENGTH_SHORT).show()
                            irAGestionAdministrador()
                        }
                        else -> {
                            Toast.makeText(this, "Tipo de usuario no reconocido", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al verificar el usuario", Toast.LENGTH_SHORT).show()
            }
    }


    private fun irASeleccionBocadillo() {
        val intent = Intent(this, BottomNavManager::class.java)
        startActivity(intent)
        finish()
    }

    private fun irAGestionAdministrador() {
        val intent = Intent(this, AdminNavManager::class.java)
        startActivity(intent)
        finish()
    }

    // Método para crear un usuario como Alumno
    private fun crearUsuarioComoAlumno(user: FirebaseUser) {
        val db = Firebase.firestore
        val email = user.email ?: return

        val usuario = hashMapOf(
            "tipo_usuario" to "Alumno",
            "nombre" to user.displayName,
            "email" to email
        )

        db.collection("alumnos").document(email).set(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario creado como Alumno", Toast.LENGTH_SHORT).show()
                irASeleccionBocadillo()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
            }
    }

    // Método para crear un usuario como Cocina
    private fun crearUsuarioComoCocina(user: FirebaseUser) {
        val db = Firebase.firestore
        val email = user.email ?: return

        val usuario = hashMapOf(
            "tipo_usuario" to "Cocina",
            "nombre" to user.displayName,
            "email" to email
        )

        db.collection("cocina").document(email).set(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario creado como Cocina", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
            }
    }

    // Método para crear un usuario como Administrador
    private fun crearUsuarioComoAdministrador(user: FirebaseUser) {
        val db = Firebase.firestore
        val email = user.email ?: return

        val usuario = hashMapOf(
            "tipo_usuario" to "Administrador",
            "nombre" to user.displayName,
            "email" to email
        )

        db.collection("administradores").document(email).set(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario creado como Administrador", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
            }
    }
}
