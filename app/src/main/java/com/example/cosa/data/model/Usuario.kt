package com.example.cosa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val correo: String,
    val pass: String,
    val nombre: String,
    val rol: String,
    val activo: Boolean
) {
}