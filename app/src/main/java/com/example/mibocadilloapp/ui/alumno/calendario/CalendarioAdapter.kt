package com.example.mibocadilloapp.ui.alumno.calendario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadilloapp.R

class CalendarioAdapter(private val bocadillos: List<BocadilloSemana>) :
    RecyclerView.Adapter<CalendarioAdapter.BocadilloViewHolder>() {

    class BocadilloViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombreBocadillo)
        val txtTipo: TextView = view.findViewById(R.id.txtTipoBocadillo)
        val txtDia: TextView = view.findViewById(R.id.txtDiaBocadillo)
        val txtIngredientes: TextView = view.findViewById(R.id.txtIngredientesBocadillo)
        val txtAlergenos: TextView = view.findViewById(R.id.txtAlergenosBocadillo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BocadilloViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bocadillo_semana, parent, false)
        return BocadilloViewHolder(view)
    }

    override fun onBindViewHolder(holder: BocadilloViewHolder, position: Int) {
        val bocadillo = bocadillos[position]
        holder.txtNombre.text = bocadillo.nombre
        holder.txtTipo.text = "Tipo: ${bocadillo.tipo.capitalize()}"
        holder.txtDia.text = "Día: ${bocadillo.dia.capitalize()}"
        holder.txtIngredientes.text = "Ingredientes: ${bocadillo.ingredientes}"
        holder.txtAlergenos.text = "Alergenos: ${bocadillo.alergenos}"
    }

    override fun getItemCount() = bocadillos.size
}
