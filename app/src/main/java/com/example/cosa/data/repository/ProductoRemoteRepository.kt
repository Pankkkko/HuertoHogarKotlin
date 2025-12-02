package com.example.cosa.data.repository

import android.util.Log
import com.example.cosa.data.model.Producto
import com.example.cosa.data.remote.api.ProductoApi
import com.example.cosa.data.remote.dto.aModelo
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
                Log.d(TAG, "API OK: obtenidos ${listaDto.size} productos")
                Result.success(listaDto.map { it.aModelo() })
            } else {
                Log.w(TAG, "API HTTP error: ${respuesta.code()} ${respuesta.message()}")
                Result.failure(IOException("HTTP ${respuesta.code()} ${respuesta.message()}"))
            }
        } catch (ex: IOException) {
            Log.e(TAG, "IO error al obtener productos: ${ex.message}")
            Result.failure(ex)
        } catch (ex: Exception) {
            Log.e(TAG, "Error inesperado al obtener productos: ${ex.message}")
            Result.failure(ex)
        }
    }

    suspend fun obtenerPorCategoria(categoria: String): Result<List<Producto>> = withContext(Dispatchers.IO) {
        try {
            val respuesta = api.obtenerPorCategoria(categoria)
            if (respuesta.isSuccessful) {
                val listaDto = respuesta.body() ?: emptyList()
                Log.d(TAG, "API OK categoria=$categoria: ${listaDto.size} items")
                Result.success(listaDto.map { it.aModelo() })
            } else {
                Log.w(TAG, "API HTTP error categoria: ${respuesta.code()} ${respuesta.message()}")
                Result.failure(IOException("HTTP ${respuesta.code()} ${respuesta.message()}"))
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error al obtener por categoria: ${ex.message}")
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
                Log.w(TAG, "API HTTP error id=$id: ${respuesta.code()} ${respuesta.message()}")
                Result.failure(IOException("HTTP ${respuesta.code()} ${respuesta.message()}"))
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error al obtener por id: ${ex.message}")
            Result.failure(ex)
        }
    }
}
