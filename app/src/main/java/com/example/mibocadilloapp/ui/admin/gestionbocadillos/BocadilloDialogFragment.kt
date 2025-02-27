package com.example.mibocadilloapp.ui.admin.bocadillos

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.mibocadilloapp.R
import java.text.SimpleDateFormat
import java.util.*

class BocadilloDialogFragment(
    private val bocadillo: Bocadillo? = null,
    private val onGuardar: (Bocadillo) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_bocadillo, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombreBocadillo)
        val etIngredientes = view.findViewById<EditText>(R.id.etIngredientesBocadillo)
        val etAlergenos = view.findViewById<EditText>(R.id.etAlergenosBocadillo)
        val etPrecio = view.findViewById<EditText>(R.id.etPrecioBocadillo)
        val spTipo = view.findViewById<Spinner>(R.id.spinnerTipoBocadillo)
        val spDia = view.findViewById<Spinner>(R.id.spinnerDia)
        val switchActivo = view.findViewById<Switch>(R.id.switchActivo)

        // Llenar datos si es edición
        bocadillo?.let {
            etNombre.setText(it.nombre)
            etIngredientes.setText(it.ingredientes)
            etAlergenos.setText(it.alergenos)
            etPrecio.setText(it.precio)
            spTipo.setSelection(if (it.tipo_bocadillo == "frio") 0 else 1)
            spDia.setSelection(obtenerIndiceDia(it.dia))

            switchActivo.isChecked = it.fecha_baja == "null"
        }

        builder.setView(view)
            .setTitle(if (bocadillo == null) "Añadir Bocadillo" else "Editar Bocadillo")
            .setPositiveButton("Guardar", null) // ❌ No cerrar automáticamente
            .setNegativeButton("Cancelar", null)

        val alertDialog = builder.create()

        // ⬇️ Se configura el botón después de la creación del diálogo
        alertDialog.setOnShowListener {
            val botonGuardar = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            botonGuardar.setOnClickListener {
                val nombre = etNombre.text.toString().trim()
                val ingredientes = etIngredientes.text.toString().trim()
                val alergenos = etAlergenos.text.toString().trim()
                val precio = etPrecio.text.toString().trim()
                val tipo = spTipo.selectedItem.toString()
                val dia = spDia.selectedItem.toString()

                // ✅ Validar que todos los campos tengan datos
                if (nombre.isEmpty() || ingredientes.isEmpty() || alergenos.isEmpty() || precio.isEmpty()) {
                    Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener // ❌ No cerrar el diálogo
                }

                // ✅ Validar que el precio sea un número válido
                if (precio.toDoubleOrNull() == null) {
                    Toast.makeText(requireContext(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener // ❌ No cerrar el diálogo
                }

                val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val nuevoBocadillo = Bocadillo(
                    nombre = nombre,
                    ingredientes = ingredientes,
                    alergenos = alergenos,
                    precio = precio,
                    tipo_bocadillo = tipo,
                    dia = dia,
                    fecha_baja = if (switchActivo.isChecked) "null" else fechaActual
                )

                onGuardar(nuevoBocadillo)
                alertDialog.dismiss() // ✅ Cerrar el diálogo solo si todo está correcto
            }
        }

        return alertDialog
    }

    private fun obtenerIndiceDia(dia: String): Int {
        return when (dia.lowercase()) {
            "lunes" -> 0
            "martes" -> 1
            "miércoles" -> 2
            "jueves" -> 3
            "viernes" -> 4
            else -> 0
        }
    }
}
