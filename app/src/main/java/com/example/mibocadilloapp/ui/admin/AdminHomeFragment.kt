package com.example.mibocadilloapp.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.mibocadilloapp.MainActivity
import com.example.mibocadilloapp.R
import com.example.mibocadilloapp.ui.admin.bocadillos.GestionBocadillosFragment
import com.example.mibocadilloapp.ui.admin.gestionalumno.GestionAlumnosFragment
import com.google.firebase.auth.FirebaseAuth

class AdminHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_home, container, false)

        val btnGestionBocadillos: Button = view.findViewById(R.id.btnGestionBocadillos)
        val btnGestionAlumnos: Button = view.findViewById(R.id.btnGestionAlumnos)
        val btnCerrarSesion: Button = view.findViewById(R.id.btnCerrarSesion) // ✅ Botón cerrar sesión

        btnGestionBocadillos.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerAdmin, GestionBocadillosFragment())
                .addToBackStack(null)
                .commit()
        }

        btnGestionAlumnos.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerAdmin, GestionAlumnosFragment())
                .addToBackStack(null)
                .commit()
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion() // ✅ Llamamos a la función de logout
        }

        return view
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut() // ✅ Cierra la sesión en Firebase

        // ✅ Redirigir a la pantalla de inicio de sesión
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
