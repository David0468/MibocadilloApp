package com.example.mibocadilloapp.ui.alumno.calendario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadilloapp.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CalendarioFragment : Fragment() {

    private lateinit var txtBocadilloFrioManana: TextView
    private lateinit var txtBocadilloCalienteManana: TextView
    private lateinit var recyclerCalendario: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private lateinit var calendarioAdapter: CalendarioAdapter
    private val listaBocadillosSemana = mutableListOf<BocadilloSemana>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendario, container, false)

        // Inicializar vistas
        txtBocadilloFrioManana = view.findViewById(R.id.txtBocadilloFrioManana)
        txtBocadilloCalienteManana = view.findViewById(R.id.txtBocadilloCalienteManana)
        recyclerCalendario = view.findViewById(R.id.recyclerCalendario)

        // Configurar RecyclerView
        calendarioAdapter = CalendarioAdapter(listaBocadillosSemana)
        recyclerCalendario.layoutManager = LinearLayoutManager(context)
        recyclerCalendario.adapter = calendarioAdapter

        // Cargar bocadillos para mañana y la semana
        cargarBocadillosParaManana()
        cargarBocadillosDeLaSemana()

        return view
    }

    private fun cargarBocadillosParaManana() {
        val diaSiguiente = obtenerDiaSiguiente()

        db.collection("bocadillos")
            .whereEqualTo("dia", diaSiguiente)
            .get()
            .addOnSuccessListener { documentos ->
                for (document in documentos) {
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val tipo = document.getString("tipo_bocadillo") ?: ""

                    if (tipo == "frio") {
                        txtBocadilloFrioManana.text = nombre
                    } else if (tipo == "caliente") {
                        txtBocadilloCalienteManana.text = nombre
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar los bocadillos de mañana", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarBocadillosDeLaSemana() {
        db.collection("bocadillos")
            .get()
            .addOnSuccessListener { documentos ->
                listaBocadillosSemana.clear()

                for (document in documentos) {
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val tipo = document.getString("tipo_bocadillo") ?: "Desconocido"
                    val dia = document.getString("dia") ?: "Día no especificado"
                    val ingredientes = document.getString("ingredientes") ?: "Sin ingredientes"
                    val alergenos = document.getString("alergenos") ?: "Sin alérgenos"

                    val bocadillo = BocadilloSemana(nombre, tipo, dia, ingredientes, alergenos)
                    listaBocadillosSemana.add(bocadillo)
                }

                // Actualizar la lista en el RecyclerView
                calendarioAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar los bocadillos de la semana", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerDiaSiguiente(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Suma 1 día
        return SimpleDateFormat("EEEE", Locale("es", "ES")).format(calendar.time).lowercase()
    }
}
