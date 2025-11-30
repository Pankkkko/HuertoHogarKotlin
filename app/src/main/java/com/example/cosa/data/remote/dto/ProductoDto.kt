package com.example.cosa.data.remote.dto

import com.example.cosa.data.model.Producto
import com.google.gson.annotations.SerializedName
import com.example.cosa.data.Enum.CategoriaENUM

data class ProductoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("image") val image: String,
    @SerializedName("category") val category: String
)

fun ProductoDto.aModelo(): Producto {
    return Producto(
        id = id.toString(),
        nombre = title,
        descripcion = description,
        precio = price,
        imagen1 = image,
        imagen2 = image,
        imagen3 = image,
        imagen4 = image,
        stock = 10,
        categoria = CategoriaENUM.PRODUCTOS_ORGANICOS // mapeo por defecto; se puede mejorar
    )
}

fun Producto.aDto(): ProductoDto {
    return ProductoDto(
        id = id.toIntOrNull() ?: 0,
        title = nombre,
        description = descripcion,
        price = precio,
        image = imagen1,
        category = categoria.name
    )
}

