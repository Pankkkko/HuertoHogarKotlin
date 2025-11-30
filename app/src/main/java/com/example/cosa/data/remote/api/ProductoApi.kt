package com.example.cosa.data.remote.api

import com.example.cosa.data.remote.dto.ProductoDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductoApi {
    @GET("products")
    suspend fun obtenerTodos(): List<ProductoDto>

    @GET("products/{id}")
    suspend fun obtenerPorId(@Path("id") id: Int): ProductoDto

    @GET("products/category/{categoria}")
    suspend fun obtenerPorCategoria(@Path("categoria") categoria: String): List<ProductoDto>
}

