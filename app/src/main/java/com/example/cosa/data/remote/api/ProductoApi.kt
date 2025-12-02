package com.example.cosa.data.remote.api

import com.example.cosa.data.remote.dto.ProductoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductoApi {
    @GET("products")
    suspend fun obtenerTodos(): Response<List<ProductoDto>>

    @GET("products/{id}")
    suspend fun obtenerPorId(@Path("id") id: Int): Response<ProductoDto>

    @GET("products/categoria/{categoria}")
    suspend fun obtenerPorCategoria(@Path("categoria") categoria: String): Response<List<ProductoDto>>
}
