package com.example.cosa.data.repository

import com.example.cosa.data.model.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class UsuarioRepositoryFallbackTest {

    // DAO en memoria para tests
    class InMemoryUsuarioDAO : com.example.cosa.data.dao.UsuarioDAO {
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

    @Test
    fun registrar_remote_exito_inserta_usuario_devuelto_por_remoto() = runBlocking {
        val dao = InMemoryUsuarioDAO()
        val remote = mockk<UsuarioRemoteRepository>()

        val created = Usuario(id = 99, rut = "r", usuario = "u_rem", correo = "rem@dom.cl", pass = "p")
        coEvery { remote.crear(any()) } returns Result.success(created)

        val repo = UsuarioRepository(dao, remote)
        val toCreate = Usuario(rut = "r", usuario = "u", correo = "c@dom.cl", pass = "p")

        repo.registrar(toCreate)

        val all = dao.getAll()
        assertEquals(1, all.size)
        // se insertó el usuario devuelto por remoto (se copia con nuevo id por el DAO en memoria)
        assertEquals("rem@dom.cl", all[0].correo)

        coVerify(exactly = 1) { remote.crear(any()) }
    }

    @Test
    fun registrar_remote_falla_inserta_local() = runBlocking {
        val dao = InMemoryUsuarioDAO()
        val remote = mockk<UsuarioRemoteRepository>()

        coEvery { remote.crear(any()) } returns Result.failure(Exception("no net"))

        val repo = UsuarioRepository(dao, remote)
        val toCreate = Usuario(rut = "r", usuario = "u_local", correo = "local@dom.cl", pass = "p")

        repo.registrar(toCreate)

        val all = dao.getAll()
        assertEquals(1, all.size)
        assertEquals("local@dom.cl", all[0].correo)

        coVerify(exactly = 1) { remote.crear(any()) }
    }

    @Test
    fun obtenerPorCorreo_si_no_local_busca_en_remoto_y_cachea() = runBlocking {
        val dao = InMemoryUsuarioDAO()
        val remote = mockk<UsuarioRemoteRepository>()

        val remoteUser = Usuario(id = 5, rut = "r5", usuario = "u5", correo = "r5@dom.cl", pass = "p")
        coEvery { remote.obtenerTodos() } returns Result.success(listOf(remoteUser))

        val repo = UsuarioRepository(dao, remote)

        val found = repo.obtenerPorCorreo("r5@dom.cl")
        assertNotNull(found)
        assertEquals("r5@dom.cl", found!!.correo)

        // Debe haberse cacheado en DAO
        val all = dao.getAll()
        assertEquals(1, all.size)
        assertEquals("r5@dom.cl", all[0].correo)

        coVerify(exactly = 1) { remote.obtenerTodos() }
    }
}

