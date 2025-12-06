package com.example.cosa.data.remote.api

import com.example.cosa.data.remote.dto.ProductoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

interface ProductoApi {
    @GET("/api/productos")
    suspend fun obtenerTodos(): Response<List<ProductoDto>>

    @GET("/api/productos/{id}")
    suspend fun obtenerPorId(@Path("id") id: Int): Response<ProductoDto>


    @POST("/api/productos")
    suspend fun crear(@Body producto: ProductoDto): Response<ProductoDto>
}
