package com.example.cosa.data.repository

import android.util.Log
import com.example.cosa.data.model.Producto
import com.example.cosa.data.remote.api.ProductoApi
import com.example.cosa.data.remote.dto.aModelo
import com.example.cosa.data.remote.dto.aDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ProductoRemoteRepository(private val api: ProductoApi) {

    private val TAG = "ProductoRemoteRepo"

    suspend fun obtenerTodos(): Result<List<Producto>> = withContext(Dispatchers.IO) {
        try {
            val respuesta = api.obtenerTodos()
            if (respuesta.isSuccessful) {
                val listaDto = respuesta.body() ?: emptyList()
                Log.d(TAG, "API OK: obtenidos ${'$'}{listaDto.size} productos")
                Result.success(listaDto.map { it.aModelo() })
            } else {
                Log.w(TAG, "API HTTP error: ${'$'}{respuesta.code()} ${'$'}{respuesta.message()}")
                Result.failure(IOException("HTTP ${'$'}{respuesta.code()} ${'$'}{respuesta.message()}"))
            }
        } catch (ex: IOException) {
            Log.e(TAG, "IO error al obtener productos: ${ex.message}")
            Result.failure(ex)
        } catch (ex: Exception) {
            Log.e(TAG, "Error inesperado al obtener productos: ${ex.message}")
            Result.failure(ex)
        }
    }



    suspend fun obtenerPorId(id: Int): Result<Producto> = withContext(Dispatchers.IO) {
        try {
            val respuesta = api.obtenerPorId(id)
            if (respuesta.isSuccessful) {
                val dto = respuesta.body()!!
                Log.d(TAG, "API OK id=$id")
                Result.success(dto.aModelo())
            } else {
                Log.w(TAG, "API HTTP error id=$id: ${'$'}{respuesta.code()} ${'$'}{respuesta.message()}" )
                Result.failure(IOException("HTTP ${'$'}{respuesta.code()} ${'$'}{respuesta.message()}"))
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error al obtener por id: ${'$'}{ex.message}")
            Result.failure(ex)
        }
    }

    // Nuevo: crear producto remoto (POST)
    suspend fun crear(producto: Producto): Result<Producto> = withContext(Dispatchers.IO) {
        try {
            // mapear a DTO
            val dto = producto.aDto()
            val respuesta = api.crear(dto)
            if (respuesta.isSuccessful) {
                val creadoDto = respuesta.body()!!
                Log.d(TAG, "API OK crear: ${'$'}{creadoDto.id}")
                Result.success(creadoDto.aModelo())
            } else {
                Log.w(TAG, "API HTTP error crear: ${'$'}{respuesta.code()} ${'$'}{respuesta.message()}")
                Result.failure(IOException("HTTP ${'$'}{respuesta.code()} ${'$'}{respuesta.message()}"))
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error al crear producto remoto: ${'$'}{ex.message}")
            Result.failure(ex)
        }
    }
}
