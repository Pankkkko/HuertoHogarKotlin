package com.example.cosa.presentation.ui.Components

fun imagenUrl(nombre: String?): String {
    if (nombre.isNullOrBlank()) return ""

    return if (nombre.startsWith("http")) {
        nombre
    } else {
        "http://10.0.2.2:8080/uploads/$nombre"
    }
}