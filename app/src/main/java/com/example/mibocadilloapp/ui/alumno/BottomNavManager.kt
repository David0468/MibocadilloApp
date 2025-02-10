package com.example.mibocadilloapp.ui.alumno

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mibocadilloapp.R
import com.example.mibocadilloapp.ui.alumno.perfil.PerfilFragment
import com.example.mibocadilloapp.ui.alumno.calendario.CalendarioFragment
import com.example.mibocadilloapp.ui.alumno.historial.HistorialFragment
import com.example.mibocadilloapp.ui.alumno.seleccion_bocadillo.SeleccionBocadillo
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavManager : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav_manager)

        bottomNav = findViewById(R.id.bottomNavigationView)

        // Establecer SeleccionBocadilloFragment como el fragmento inicial
        replaceFragment(SeleccionBocadillo())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_seleccion_bocadillo -> replaceFragment(SeleccionBocadillo())
                R.id.nav_perfil -> replaceFragment(PerfilFragment())
                R.id.nav_calendario -> replaceFragment(CalendarioFragment())
                R.id.nav_historial -> replaceFragment(HistorialFragment())
            }
            true
        }
    }

    // Función para cambiar de fragmento
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
