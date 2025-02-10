package com.example.mibocadilloapp.ui.alumno.perfil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mibocadilloapp.MainActivity
import com.example.mibocadilloapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtCorreo = view.findViewById<TextView>(R.id.txtCorreo)
        val btnCerrarSesion = view.findViewById<Button>(R.id.btnCerrarSesion)

        // Obtener el usuario actual
        val usuario = FirebaseAuth.getInstance().currentUser
        if (usuario != null) {
            txtCorreo.text = usuario.email // Mostrar el correo en el TextView
            obtenerNombreUsuario(usuario.email!!, txtNombre) // Obtener el nombre desde Firestore
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        return view
    }

    private fun obtenerNombreUsuario(email: String, txtNombre: TextView) {
        val db = FirebaseFirestore.getInstance()
        db.collection("alumnos").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombre = document.getString("nombre")
                    txtNombre.text = nombre ?: "Nombre no disponible"
                } else {
                    txtNombre.text = "Nombre no encontrado"
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Error al obtener el nombre", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
