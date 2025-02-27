package com.example.mibocadilloapp.ui.admin.bocadillos

data class Bocadillo(
    var nombre: String = "",
    var ingredientes: String = "",
    var alergenos: String = "",
    var tipo_bocadillo: String = "",
    var dia: String = "",
    var precio: String = "",
    var fecha_baja: String = "null" // Asegurar que coincide con Firestore
)
