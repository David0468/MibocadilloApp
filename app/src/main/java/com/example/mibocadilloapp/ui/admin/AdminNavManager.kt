package com.example.mibocadilloapp.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mibocadilloapp.R

class AdminNavManager : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_nav_manager)

        // Cargar el fragmento principal del administrador
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerAdmin, AdminHomeFragment())
            .commit()
    }
}
