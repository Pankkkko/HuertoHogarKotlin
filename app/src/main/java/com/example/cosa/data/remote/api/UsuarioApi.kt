package com.example.cosa.data.remote.api

import com.example.cosa.data.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApi {
    @GET("usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioDto>

    @Suppress("unused")
    @GET("usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Int): UsuarioDto

    @POST("usuarios")
    suspend fun crearUsuario(@Body usuario: UsuarioDto): UsuarioDto

    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Int, @Body usuario: UsuarioDto): UsuarioDto

    @DELETE("usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Int)
}
