package com.example.mibocadilloapp.ui.alumno.historial

data class PedidoHistorial(
    val nombre: String,
    val tipo: String,
    val ingredientes: String,
    val alergenos: String,
    val precio: Double,
    val fecha: String
)
