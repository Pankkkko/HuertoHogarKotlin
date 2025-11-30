package com.example.cosa.data.repository

import com.example.cosa.data.model.Producto
import com.example.cosa.data.remote.api.ProductoApi
import com.example.cosa.data.remote.dto.aModelo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ProductoRemoteRepository(private val api: ProductoApi) {

    suspend fun obtenerTodos(): Result<List<Producto>> = withContext(Dispatchers.IO) {
        try {
            val listaDto = api.obtenerTodos()
            Result.success(listaDto.map { it.aModelo() })
        } catch (ex: IOException) {
            Result.failure(ex)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun obtenerPorCategoria(categoria: String): Result<List<Producto>> = withContext(Dispatchers.IO) {
        try {
            val listaDto = api.obtenerPorCategoria(categoria)
            Result.success(listaDto.map { it.aModelo() })
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun obtenerPorId(id: Int): Result<Producto> = withContext(Dispatchers.IO) {
        try {
            val dto = api.obtenerPorId(id)
            Result.success(dto.aModelo())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}

