package com.example.mibocadilloapp.ui.alumno.historial

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadilloapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistorialFragment : Fragment() {

    private lateinit var txtTotalMensual: TextView
    private lateinit var txtBocadillosFrio: TextView
    private lateinit var txtBocadillosCaliente: TextView
    private lateinit var recyclerHistorial: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var historialAdapter: HistorialAdapter
    private val listaPedidos = mutableListOf<PedidoHistorial>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial, container, false)

        // Inicializar vistas
        txtTotalMensual = view.findViewById(R.id.txtTotalMensual)
        txtBocadillosFrio = view.findViewById(R.id.txtBocadillosFrio)
        txtBocadillosCaliente = view.findViewById(R.id.txtBocadillosCaliente)
        recyclerHistorial = view.findViewById(R.id.recyclerHistorial)

        // Configurar RecyclerView
        historialAdapter = HistorialAdapter(listaPedidos)
        recyclerHistorial.layoutManager = LinearLayoutManager(context)
        recyclerHistorial.adapter = historialAdapter

        // Cargar pedidos del usuario
        cargarPedidos()

        return view
    }

    private fun cargarPedidos() {
        val usuario = auth.currentUser
        if (usuario == null) {
            Toast.makeText(context, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        val email = usuario.email ?: return

        db.collection("pedidos")
            .whereEqualTo("id_alumno", email)
            .get()
            .addOnSuccessListener { pedidosSnapshot ->
                if (pedidosSnapshot.isEmpty) {
                    Toast.makeText(context, "No tienes pedidos registrados.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Mostrar el número total de pedidos encontrados
                Log.d("HistorialFragment", "Total de pedidos encontrados: ${pedidosSnapshot.size()}")

                var totalFrio = 0
                var totalCaliente = 0
                var totalGastado = 0.0

                listaPedidos.clear() // Limpiar lista antes de agregar nuevos datos

                for (pedidoDoc in pedidosSnapshot) {
                    val nombreBocadillo = pedidoDoc.getString("id_bocadillo") ?: ""
                    val precio = when (val precioValue = pedidoDoc.get("precio")) {
                        is String -> precioValue.toDoubleOrNull() ?: 0.0
                        is Double -> precioValue
                        else -> 0.0
                    }

                    val fechaPedido = pedidoDoc.getString("fecha_pedido") ?: ""

                    // Obtener datos del bocadillo desde Firestore
                    db.collection("bocadillos")
                        .document(nombreBocadillo)
                        .get()
                        .addOnSuccessListener { bocadilloDoc ->
                            if (bocadilloDoc.exists()) {
                                val tipoBocadillo = bocadilloDoc.getString("tipo_bocadillo") ?: ""
                                val ingredientes = bocadilloDoc.getString("ingredientes") ?: ""
                                val alergenos = bocadilloDoc.getString("alergenos") ?: ""

                                // Clasificar por tipo de bocadillo
                                if (tipoBocadillo == "frio") totalFrio++
                                else if (tipoBocadillo == "caliente") totalCaliente++

                                totalGastado += precio

                                // Agregar a la lista de pedidos
                                val pedido = PedidoHistorial(
                                    nombreBocadillo, tipoBocadillo, ingredientes, alergenos, precio, fechaPedido
                                )
                                listaPedidos.add(pedido)
                                historialAdapter.notifyDataSetChanged()

                                // Actualizar contadores en pantalla
                                txtBocadillosFrio.text = totalFrio.toString()
                                txtBocadillosCaliente.text = totalCaliente.toString()
                                txtTotalMensual.text = "€${"%.2f".format(totalGastado)}"
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar el historial.", Toast.LENGTH_SHORT).show()
            }
    }
    private fun obtenerRangoFechasMesActual(): Pair<String, String> {
        val calendar = Calendar.getInstance()

        // Obtener el primer día del mes
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val primerDia = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        // Obtener el último día del mes
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val ultimoDia = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        return Pair(primerDia, ultimoDia)
    }
}
