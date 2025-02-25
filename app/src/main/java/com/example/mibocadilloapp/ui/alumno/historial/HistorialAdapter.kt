package com.example.mibocadilloapp.ui.alumno.historial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mibocadilloapp.R

class HistorialAdapter(private val pedidos: List<PedidoHistorial>) :
    RecyclerView.Adapter<HistorialAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombrePedido)
        val txtTipo: TextView = view.findViewById(R.id.txtTipoPedido)
        val txtIngredientes: TextView = view.findViewById(R.id.txtIngredientesPedido)
        val txtAlergenos: TextView = view.findViewById(R.id.txtAlergenosPedido)
        val txtPrecio: TextView = view.findViewById(R.id.txtPrecioPedido)
        val txtFecha: TextView = view.findViewById(R.id.txtFechaPedido)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.txtNombre.text = pedido.nombre
        holder.txtTipo.text = "Tipo: ${pedido.tipo.capitalize()}"
        holder.txtIngredientes.text = "Ingredientes: ${pedido.ingredientes}"
        holder.txtAlergenos.text = "Alergenos: ${pedido.alergenos}"
        holder.txtPrecio.text = "Precio: €${"%.2f".format(pedido.precio)}"
        holder.txtFecha.text = "Fecha: ${pedido.fecha}"
    }

    override fun getItemCount() = pedidos.size
}
