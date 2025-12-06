package com.example.cosa.data.model

import com.example.cosa.data.Enum.CategoriaENUM
import java.text.NumberFormat
import java.util.Locale

data class Producto(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagen1: String,
    val imagen2 : String,
    val imagen3 : String,
    val imagen4 : String,
    val stock: Int,
    val categoria: String
) {
    val disponible:Boolean
        get() = stock > 0

    val precioFormateado: String
        get() {
            val formatoChileno = NumberFormat.getNumberInstance(Locale("es", "CL"))
            formatoChileno.maximumFractionDigits = 0
            return "$${formatoChileno.format(precio)}"
        }
}