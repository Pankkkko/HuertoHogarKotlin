package com.example.cosa.data.repository

import com.example.cosa.data.model.Usuario
import com.example.cosa.data.remote.api.UsuarioApi
import com.example.cosa.data.remote.dto.UsuarioDto
import com.example.cosa.data.remote.dto.aDominio
import com.example.cosa.data.remote.dto.aDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class UsuarioRemoteRepository(private val api: UsuarioApi) {

    suspend fun obtenerTodos(): Result<List<Usuario>> = withContext(Dispatchers.IO) {
        try {
            val listaDto: List<UsuarioDto> = api.obtenerUsuarios()
            Result.success(listaDto.map { it.aDominio() })
        } catch (ex: IOException) {
            Result.failure(ex)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun crear(usuario: Usuario): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            val dto = api.crearUsuario(usuario.aDto())
            Result.success(dto.aDominio())
        } catch (ex: IOException) {
            Result.failure(ex)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun actualizar(id: Int, usuario: Usuario): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            val dto = api.actualizarUsuario(id, usuario.aDto())
            Result.success(dto.aDominio())
        } catch (ex: IOException) {
            Result.failure(ex)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun eliminar(id: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            api.eliminarUsuario(id)
            Result.success(true)
        } catch (ex: IOException) {
            Result.failure(ex)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
