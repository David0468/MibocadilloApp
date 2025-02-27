package com.example.mibocadilloapp.ui.admin.bocadillos

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadilloapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class GestionBocadillosFragment : Fragment() {

    private lateinit var recyclerBocadillos: RecyclerView
    private lateinit var fabAgregarBocadillo: FloatingActionButton
    private lateinit var bocadilloAdapter: BocadilloAdapter
    private val listaBocadillos = mutableListOf<Bocadillo>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gestion_bocadillos, container, false)

        recyclerBocadillos = view.findViewById(R.id.recyclerBocadillos)
        fabAgregarBocadillo = view.findViewById(R.id.fabAgregarBocadillo)

        recyclerBocadillos.layoutManager = LinearLayoutManager(context)
        bocadilloAdapter = BocadilloAdapter(listaBocadillos, ::editarBocadillo, ::confirmarEliminacion)
        recyclerBocadillos.adapter = bocadilloAdapter

        cargarBocadillos()

        fabAgregarBocadillo.setOnClickListener {
            mostrarDialogoAgregarBocadillo()
        }

        return view
    }

    private fun cargarBocadillos() {
        db.collection("bocadillos").get()
            .addOnSuccessListener { documentos ->
                listaBocadillos.clear()
                for (document in documentos) {
                    val bocadillo = Bocadillo(
                        nombre = document.getString("nombre") ?: "",
                        ingredientes = document.getString("ingredientes") ?: "",
                        alergenos = document.getString("alergenos") ?: "",
                        tipo_bocadillo = document.getString("tipo_bocadillo") ?: "",
                        dia = document.getString("dia") ?: "",
                        precio = document.getString("precio") ?: "",
                        fecha_baja = document.getString("fecha_baja") ?: "null"
                    )
                    listaBocadillos.add(bocadillo)
                }
                bocadilloAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar bocadillos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoAgregarBocadillo() {
        val dialog = BocadilloDialogFragment { nuevoBocadillo ->
            db.collection("bocadillos").document(nuevoBocadillo.nombre).set(nuevoBocadillo)
                .addOnSuccessListener {
                    cargarBocadillos()
                    Toast.makeText(context, "Bocadillo agregado con éxito", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al agregar bocadillo", Toast.LENGTH_SHORT).show()
                }
        }
        dialog.show(parentFragmentManager, "AgregarBocadillo")
    }

    private fun editarBocadillo(bocadillo: Bocadillo) {
        val dialog = BocadilloDialogFragment(bocadillo) { bocadilloEditado ->
            db.collection("bocadillos").document(bocadillo.nombre).set(bocadilloEditado)
                .addOnSuccessListener {
                    cargarBocadillos()
                    Toast.makeText(context, "Bocadillo actualizado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al actualizar bocadillo", Toast.LENGTH_SHORT).show()
                }
        }
        dialog.show(parentFragmentManager, "EditarBocadillo")
    }

    private fun confirmarEliminacion(bocadillo: Bocadillo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Bocadillo")
            .setMessage("¿Estás seguro de que deseas eliminar '${bocadillo.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarBocadillo(bocadillo) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarBocadillo(bocadillo: Bocadillo) {
        db.collection("bocadillos").document(bocadillo.nombre).delete()
            .addOnSuccessListener {
                cargarBocadillos()
                Toast.makeText(context, "Bocadillo eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al eliminar el bocadillo", Toast.LENGTH_SHORT).show()
            }
    }
}
