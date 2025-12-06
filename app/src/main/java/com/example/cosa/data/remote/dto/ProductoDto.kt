package com.example.cosa.data.remote.dto

import com.example.cosa.data.model.Producto
import com.google.gson.annotations.SerializedName
import com.example.cosa.data.Enum.CategoriaENUM

data class ProductoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("precio") val precio: Double,
    @SerializedName("stock") val stock: Int,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("activo") val activo: Boolean,
    @SerializedName("imagen") val imagen: String,
    @SerializedName("imagen2") val imagen2: String,
    @SerializedName("imagen3") val imagen3: String,
    @SerializedName("imagen4") val imagen4: String
)
private fun fixUrl(url: String): String {
    return if (url.startsWith("http")) url
    else "http://10.0.2.2:8080/uploads$url"
}
fun ProductoDto.aModelo(): Producto {


    return Producto(
        id = id.toString(),
        nombre = nombre,
        descripcion = descripcion,
        precio = precio,
        imagen1 = fixUrl(imagen),
        imagen2 = fixUrl(imagen2),
        imagen3 = fixUrl(imagen3),
        imagen4 = fixUrl(imagen4),
        stock = stock,
        categoria = categoria

    )
}

fun Producto.aDto(): ProductoDto {
    return ProductoDto(
        id = id.toIntOrNull() ?: 0,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio,
        imagen = fixUrl(imagen1),
        imagen2 = fixUrl(imagen2),
        imagen3 = fixUrl(imagen3),
        imagen4 = fixUrl(imagen4),
        stock = stock,
        activo = stock > 0,
        categoria = categoria
    )
}

