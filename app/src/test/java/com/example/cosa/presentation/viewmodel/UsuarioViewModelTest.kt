package com.example.cosa.presentation.viewmodel

import com.example.cosa.data.dao.UsuarioDAO
import com.example.cosa.data.model.Usuario
import com.example.cosa.data.repository.UsuarioRepository
import org.junit.Assert.*
import org.junit.Test

// Tests unitarios para las funciones de validación del ViewModel
class UsuarioViewModelTest {

    // DAO en memoria simple para inyectar en el repo (solo implementación mínima usada por los tests)
    class InMemoryUsuarioDAO : UsuarioDAO {
        private val users = mutableListOf<Usuario>()

        override suspend fun insert(usuario: Usuario) {
            val nextId = (users.maxByOrNull { it.id }?.id ?: 0) + 1
            users.add(usuario.copy(id = nextId))
        }

        override suspend fun update(usuario: Usuario) {
            val idx = users.indexOfFirst { it.id == usuario.id }
            if (idx >= 0) users[idx] = usuario
        }

        override suspend fun getUsuarioPorCorreo(correo: String): Usuario? = users.find { it.correo == correo }

        override suspend fun getUsuarioPorNombre(usuario: String): Usuario? = users.find { it.usuario == usuario }

        override suspend fun getAll(): List<Usuario> = users.toList()

        override suspend fun deleteById(id: Int) { users.removeAll { it.id == id } }

        override suspend fun updateById(id: Int, rut: String, usuario: String, correo: String, pass: String) {
            val idx = users.indexOfFirst { it.id == id }
            if (idx >= 0) users[idx] = users[idx].copy(rut = rut, usuario = usuario, correo = correo, pass = pass)
        }
    }

    // Helper para construir el ViewModel con un repo basado en la DAO en memoria
    private fun buildViewModel(): UsuarioViewModel {
        val dao = InMemoryUsuarioDAO()
        val repo = UsuarioRepository(dao)
        return UsuarioViewModel(repo)
    }

    @Test
    fun correo_valido_duoc_cl() {
        val vm = buildViewModel()
        assertTrue(vm.validarCorreo("usuario@duoc.cl"))
    }

    @Test
    fun correo_valido_profesor_duoc_cl() {
        val vm = buildViewModel()
        assertTrue(vm.validarCorreo("profesor@profesor.duoc.cl"))
    }

    @Test
    fun correo_valido_gmail_com() {
        val vm = buildViewModel()
        assertTrue(vm.validarCorreo("persona@gmail.com"))
    }

    @Test
    fun correo_invalido_otro_dominio() {
        val vm = buildViewModel()
        assertFalse(vm.validarCorreo("usuario@example.com"))
    }

    @Test
    fun correo_sin_arroba_invalido() {
        val vm = buildViewModel()
        assertFalse(vm.validarCorreo("usuarioduoc.cl"))
    }

    @Test
    fun rut_valido_calculado_devuelve_true() {
        val vm = buildViewModel()
        val cuerpo = "12345678" // 8 dígitos de ejemplo
        // calcular DV inline para evitar advertencias de parámetro constante
        var suma = 0
        var multiplicador = 2
        for (c in cuerpo.reversed()) {
            suma += (c.toString().toInt() * multiplicador)
            multiplicador = if (multiplicador < 7) multiplicador + 1 else 2
        }
        val resto = 11 - (suma % 11)
        val dv = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> resto.toString().first()
        }
        val rut = "$cuerpo-$dv"
        assertTrue(vm.validarRut(rut))
    }

    @Test
    fun rut_invalido_corto_devuelve_false() {
        val vm = buildViewModel()
        assertFalse(vm.validarRut("1-9"))
    }
}
