package com.example.cosa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosa.data.model.Usuario
import com.example.cosa.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel(private val repo: UsuarioRepository) : ViewModel() {

    fun registrarUsuario(
        rut: String,
        nombreUsuario: String,
        correo: String,
        password: String,
        repetirPassword: String,
        rol: String,
        activo: Boolean,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            // 1️⃣ Validar RUT
            if (!validarRut(rut)) {
                onResult(false, "RUT inválido")
                return@launch
            }

            // 2️⃣ Validar correo
            if (!validarCorreo(correo)) {
                onResult(false, "Correo inválido")
                return@launch
            }

            // 3️⃣ Verificar si ya existe el correo o usuario
            if (repo.existeCorreo(correo)) {
                onResult(false, "El correo ya está registrado")
                return@launch
            }

            if (repo.existeUsuario(nombreUsuario)) {
                onResult(false, "El nombre de usuario ya existe")
                return@launch
            }

            // 4️⃣ Validar que las contraseñas coincidan
            if (password != repetirPassword) {
                onResult(false, "Las contraseñas no coinciden")
                return@launch
            }

            // 5️⃣ Crear usuario y guardar
            val usuario = Usuario(
                nombre = nombreUsuario,
                correo = correo,
                pass = password,
                rol = rol,
                activo = activo
            )

            repo.registrar(usuario)
            onResult(true, "Registro exitoso 🎉")
        }
    }

    // Funciones auxiliares de validación
    public fun validarCorreo(correo: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$")
        return regex.matches(correo)
    }

    public fun validarRut(rut: String): Boolean {
        val cleanRut = rut.replace(".", "").replace("-", "").uppercase()
        if (cleanRut.length < 8) return false

        val cuerpo = cleanRut.dropLast(1)
        val dv = cleanRut.last()

        var suma = 0
        var multiplicador = 2

        for (i in cuerpo.reversed()) {
            suma += (i.toString().toInt() * multiplicador)
            multiplicador = if (multiplicador < 7) multiplicador + 1 else 2
        }

        val resto = 11 - (suma % 11)
        val dvEsperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> resto.toString().first()
        }

        return dv == dvEsperado
    }

    fun iniciarSesion(
        correo: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (!validarCorreo(correo)) {
                onResult(false, "Correo inválido")
                return@launch
            }

            val usuario = repo.obtenerPorCorreo(correo)
            if (usuario == null) {
                onResult(false, "Usuario no encontrado")
                return@launch
            }

            if (usuario.pass != password) {
                onResult(false, "Contraseña incorrecta")
                return@launch
            }

            onResult(true, "Inicio de sesión exitoso 🎉")
        }
    }

}
