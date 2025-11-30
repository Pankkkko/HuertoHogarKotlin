package com.example.cosa.data.remote.dto

import com.example.cosa.data.model.Usuario
import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("rut")
    val rut: String,

    @SerializedName("usuario")
    val usuario: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("pass")
    val pass: String
)

// Mapeos entre DTO y modelo de dominio
fun UsuarioDto.aDominio(): Usuario {
    return Usuario(
        id = this.id,
        rut = this.rut,
        usuario = this.usuario,
        correo = this.correo,
        pass = this.pass
    )
}

fun Usuario.aDto(): UsuarioDto {
    return UsuarioDto(
        id = this.id,
        rut = this.rut,
        usuario = this.usuario,
        correo = this.correo,
        pass = this.pass
    )
}

