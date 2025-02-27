package com.example.mibocadilloapp.ui.admin.bocadillos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadilloapp.R

class BocadilloAdapter(
    private val bocadillos: List<Bocadillo>,
    private val onEdit: (Bocadillo) -> Unit,
    private val onDelete: (Bocadillo) -> Unit
) : RecyclerView.Adapter<BocadilloAdapter.BocadilloViewHolder>() {

    class BocadilloViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombreBocadillo)
        val txtDetalles: TextView = view.findViewById(R.id.txtDetallesBocadillo)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BocadilloViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bocadillo, parent, false)
        return BocadilloViewHolder(view)
    }

    override fun onBindViewHolder(holder: BocadilloViewHolder, position: Int) {
        val bocadillo = bocadillos[position]
        holder.txtNombre.text = bocadillo.nombre

        // Determinar si el bocadillo está activo o inactivo
        val estado = if (bocadillo.fecha_baja == "null") "Activo" else "Desactivado"

        // Mostrar tipo, día y estado
        holder.txtDetalles.text = "Tipo: ${bocadillo.tipo_bocadillo.capitalize()} • Día: ${bocadillo.dia.capitalize()} • Estado: $estado"

        holder.btnEditar.setOnClickListener { onEdit(bocadillo) }
        holder.btnEliminar.setOnClickListener { onDelete(bocadillo) }
    }

    override fun getItemCount() = bocadillos.size
}
