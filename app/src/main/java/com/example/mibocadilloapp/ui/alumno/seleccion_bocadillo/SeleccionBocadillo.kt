package com.example.mibocadilloapp.ui.alumno.seleccion_bocadillo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mibocadilloapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SeleccionBocadillo : Fragment() {

    private lateinit var txtNombreFrio: TextView
    private lateinit var txtIngredientesFrio: TextView
    private lateinit var txtAlergenosFrio: TextView
    private lateinit var btnSeleccionarFrio: Button

    private lateinit var txtNombreCaliente: TextView
    private lateinit var txtIngredientesCaliente: TextView
    private lateinit var txtAlergenosCaliente: TextView
    private lateinit var btnSeleccionarCaliente: Button

    private lateinit var btnCancelarPedido: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var pedidoActual: String? = null // Guardará el bocadillo actual del pedido

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_seleccion_bocadillo, container, false)

        // Inicializar vistas
        txtNombreFrio = view.findViewById(R.id.txtNombreFrio)
        txtIngredientesFrio = view.findViewById(R.id.txtIngredientesFrio)
        txtAlergenosFrio = view.findViewById(R.id.txtAlergenosFrio)
        btnSeleccionarFrio = view.findViewById(R.id.btnSeleccionarFrio)

        txtNombreCaliente = view.findViewById(R.id.txtNombreCaliente)
        txtIngredientesCaliente = view.findViewById(R.id.txtIngredientesCaliente)
        txtAlergenosCaliente = view.findViewById(R.id.txtAlergenosCaliente)
        btnSeleccionarCaliente = view.findViewById(R.id.btnSeleccionarCaliente)

        btnCancelarPedido = view.findViewById(R.id.btnCancelarPedido)

        cargarBocadillosDelDia()
        verificarPedidoExistente()

        btnSeleccionarFrio.setOnClickListener { seleccionarBocadillo(txtNombreFrio.text.toString()) }
        btnSeleccionarCaliente.setOnClickListener { seleccionarBocadillo(txtNombreCaliente.text.toString()) }
        btnCancelarPedido.setOnClickListener { cancelarPedido() }

        return view
    }

    private fun cargarBocadillosDelDia() {
        val diaActual = obtenerDiaActual()

        db.collection("bocadillos")
            .whereEqualTo("dia", diaActual)
            .get()
            .addOnSuccessListener { documentos ->
                for (document in documentos) {
                    val nombre = document.getString("nombre") ?: ""

                    // Convertir string de ingredientes en lista separada por comas
                    val ingredientesStr = document.getString("ingredientes") ?: ""
                    val ingredientes = ingredientesStr.split(", ").map { it.trim() }

                    // Convertir string de alérgenos en lista separada por comas
                    val alergenosStr = document.getString("alergenos") ?: ""
                    val alergenos = alergenosStr.split(", ").map { it.trim() }

                    val tipoBocadillo = document.getString("tipo_bocadillo") ?: ""

                    if (tipoBocadillo == "frio") {
                        txtNombreFrio.text = nombre
                        txtIngredientesFrio.text = ingredientes.joinToString(", ")
                        txtAlergenosFrio.text = alergenos.joinToString(", ")
                    } else if (tipoBocadillo == "caliente") {
                        txtNombreCaliente.text = nombre
                        txtIngredientesCaliente.text = ingredientes.joinToString(", ")
                        txtAlergenosCaliente.text = alergenos.joinToString(", ")
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar bocadillos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun verificarPedidoExistente() {
        val usuario = auth.currentUser ?: return
        val email = usuario.email ?: return
        val fechaHoy = obtenerFechaHoy()

        db.collection("pedidos")
            .whereEqualTo("id_alumno", email)
            .whereEqualTo("fecha_pedido", fechaHoy)
            .get()
            .addOnSuccessListener { documentos ->
                if (!documentos.isEmpty) {
                    val pedido = documentos.documents[0]
                    pedidoActual = pedido.getString("id_bocadillo")

                    marcarPedidoSeleccionado()
                }
            }
    }

    private fun seleccionarBocadillo(nombreBocadillo: String) {
        val usuario = auth.currentUser ?: return
        val email = usuario.email ?: return
        val fechaHoy = obtenerFechaHoy()

        // Si ya tenía un pedido, eliminarlo antes de registrar el nuevo
        if (pedidoActual != null) {
            db.collection("pedidos")
                .whereEqualTo("id_alumno", email)
                .whereEqualTo("fecha_pedido", fechaHoy)
                .get()
                .addOnSuccessListener { documentos ->
                    for (document in documentos) {
                        db.collection("pedidos").document(document.id).delete()
                    }

                    registrarNuevoPedido(email, nombreBocadillo)
                }
        } else {
            registrarNuevoPedido(email, nombreBocadillo)
        }
    }

    private fun registrarNuevoPedido(email: String, nombreBocadillo: String) {
        val fechaHoy = obtenerFechaHoy()

        // Obtener los datos del alumno
        db.collection("usuarios").document(email).get()
            .addOnSuccessListener { usuarioDoc ->
                val nombreAlumno = usuarioDoc.getString("nombre") ?: "Desconocido"

                // Obtener el precio del bocadillo desde Firestore
                db.collection("bocadillos").document(nombreBocadillo).get()
                    .addOnSuccessListener { bocadilloDoc ->
                        if (bocadilloDoc.exists()) {
                            val precio = bocadilloDoc.getString("precio") ?: "0.0"

                            // Crear el pedido con todos los datos necesarios
                            val pedido = hashMapOf(
                                "id_alumno" to email,
                                "nombre_alumno" to nombreAlumno,
                                "id_bocadillo" to nombreBocadillo,
                                "precio" to precio,
                                "fecha_pedido" to fechaHoy,
                                "fecha_recogida" to null
                            )

                            // Guardar el pedido en Firestore
                            db.collection("pedidos")
                                .add(pedido)
                                .addOnSuccessListener {
                                    pedidoActual = nombreBocadillo
                                    marcarPedidoSeleccionado()
                                    Toast.makeText(context, "Pedido registrado con éxito", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al registrar el pedido", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "El bocadillo no se encontró en Firestore", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
    }

    private fun cancelarPedido() {
        val usuario = auth.currentUser ?: return
        val email = usuario.email ?: return
        val fechaHoy = obtenerFechaHoy()

        db.collection("pedidos")
            .whereEqualTo("id_alumno", email)
            .whereEqualTo("fecha_pedido", fechaHoy)
            .get()
            .addOnSuccessListener { documentos ->
                for (document in documentos) {
                    db.collection("pedidos").document(document.id).delete()
                }

                pedidoActual = null
                desmarcarPedidos()
                Toast.makeText(context, "Pedido cancelado", Toast.LENGTH_SHORT).show()
            }
    }

    private fun marcarPedidoSeleccionado() {
        if (pedidoActual == txtNombreFrio.text.toString()) {
            txtNombreFrio.setTextColor(Color.GREEN)
            txtNombreCaliente.setTextColor(Color.WHITE)
        } else if (pedidoActual == txtNombreCaliente.text.toString()) {
            txtNombreCaliente.setTextColor(Color.GREEN)
            txtNombreFrio.setTextColor(Color.WHITE)
        }
    }

    private fun desmarcarPedidos() {
        txtNombreFrio.setTextColor(Color.WHITE)
        txtNombreCaliente.setTextColor(Color.WHITE)
    }

    private fun obtenerFechaHoy() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private fun obtenerDiaActual() = SimpleDateFormat("EEEE", Locale("es", "ES")).format(Date()).lowercase()
}
