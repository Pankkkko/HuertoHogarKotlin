package com.example.cosa.data.repository

import com.example.cosa.data.dao.UsuarioDAO
import com.example.cosa.data.model.Usuario
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

// Tests unitarios para UsuarioRepository usando un DAO en memoria
class UsuarioRepositoryTest {

    // DAO en memoria simple para uso en tests
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

        // El modelo usa `nombre` en lugar de `usuario`
        override suspend fun getUsuarioPorNombre(usuario: String): Usuario? = users.find { it.nombre == usuario }

        override suspend fun getAll(): List<Usuario> = users.toList()

        override suspend fun deleteById(id: Int) { users.removeAll { it.id == id } }

        // `rut` se mapea a `rol`, `usuario` se mapea a `nombre`
        override suspend fun updateById(id: Int, rut: String, usuario: String, correo: String, pass: String) {
            val idx = users.indexOfFirst { it.id == id }
            if (idx >= 0) users[idx] = users[idx].copy(rol = rut, nombre = usuario, correo = correo, pass = pass)
        }
    }

    @Test
    fun registrar_inserta_usuario() = runBlocking {
        val dao = InMemoryUsuarioDAO()
        val repo = UsuarioRepository(dao)

        val usuario = Usuario(id = 0, correo = "u1@dom.cl", pass = "pwd", nombre = "user1", rol = "user", activo = true)
        repo.registrar(usuario)

        val all = dao.getAll()
        assertEquals(1, all.size)
        assertEquals("user1", all[0].nombre)
        assertEquals("u1@dom.cl", all[0].correo)
    }

    @Test
    fun existeCorreo_detecta_existencia_y_no_existencia() = runBlocking {
        val dao = InMemoryUsuarioDAO()
        val repo = UsuarioRepository(dao)

        val usuario = Usuario(id = 0, correo = "u2@dom.cl", pass = "pwd", nombre = "user2", rol = "user", activo = true)
        dao.insert(usuario)

        assertTrue(repo.existeCorreo("u2@dom.cl"))
        assertFalse(repo.existeCorreo("noexiste@dom.cl"))
    }

    @Test
    fun actualizarUsuario_modifica_campos() = runBlocking {
        val dao = InMemoryUsuarioDAO()
        val repo = UsuarioRepository(dao)

        val usuario = Usuario(id = 0, correo = "u3@dom.cl", pass = "pwd", nombre = "user3", rol = "user", activo = true)
        dao.insert(usuario)

        val inserted = dao.getAll().first()
        val actualizado = inserted.copy(nombre = "user3_new", correo = "u3_new@dom.cl")
        repo.actualizarUsuario(actualizado)

        val fetched = dao.getUsuarioPorNombre("user3_new")
        assertNotNull(fetched)
        assertEquals("u3_new@dom.cl", fetched!!.correo)
    }
}