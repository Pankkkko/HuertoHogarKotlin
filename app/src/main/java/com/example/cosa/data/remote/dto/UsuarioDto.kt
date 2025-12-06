package com.example.cosa.data.remote.dto

import com.example.cosa.data.model.Usuario
import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("email")
    val email: String,


    @SerializedName("password")
    val pass: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("activo")
    val activo: Boolean


)

// Mapeos entre DTO y modelo de dominio
fun UsuarioDto.aDominio(): Usuario {
    return Usuario(
        id = id,
        correo = email,
        pass = pass,
        nombre = nombre,
        rol = rol,
        activo = activo
    )
}

fun Usuario.aDto(): UsuarioDto {
    return UsuarioDto(
        id = id,
        email = correo,
        pass = pass,
        nombre = nombre,
        rol = rol,
        activo = activo

    )
}

