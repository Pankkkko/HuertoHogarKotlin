package com.example.cosa.data.repository

import com.example.cosa.data.remote.api.UsuarioApi
import com.example.cosa.data.remote.dto.UsuarioDto
import com.example.cosa.data.model.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class UsuarioRemoteRepositoryTest {

    private fun sampleUsuarioDto(id: Int = 1): UsuarioDto = UsuarioDto(
        id = id,
        rut = "11111111-1",
        usuario = "user$id",
        correo = "u$id@dom.cl",
        pass = "pwd"
    )

    @Test
    fun obtenerTodos_exito_devuelve_lista() = runBlocking {
        val api = mockk<UsuarioApi>()
        val dtoList = listOf(sampleUsuarioDto(1), sampleUsuarioDto(2))
        coEvery { api.obtenerUsuarios() } returns dtoList

        val repo = UsuarioRemoteRepository(api)
        val res = repo.obtenerTodos()

        assertTrue(res.isSuccess)
        val lista = res.getOrNull()
        assertNotNull(lista)
        assertEquals(2, lista!!.size)
        assertEquals("u1@dom.cl", lista[0].correo)

        coVerify(exactly = 1) { api.obtenerUsuarios() }
    }

    @Test
    fun obtenerTodos_error_de_red_devuelve_failure() = runBlocking {
        val api = mockk<UsuarioApi>()
        coEvery { api.obtenerUsuarios() } throws IOException("no net")

        val repo = UsuarioRemoteRepository(api)
        val res = repo.obtenerTodos()

        assertTrue(res.isFailure)
        coVerify(exactly = 1) { api.obtenerUsuarios() }
    }

    @Test
    fun crear_exito_devuelve_usuario_creado() = runBlocking {
        val api = mockk<UsuarioApi>()
        val dto = sampleUsuarioDto(5)
        coEvery { api.crearUsuario(any()) } returns dto

        val repo = UsuarioRemoteRepository(api)
        val usuario = Usuario(rut = dto.rut, usuario = dto.usuario, correo = dto.correo, pass = dto.pass)
        val res = repo.crear(usuario)

        assertTrue(res.isSuccess)
        val creado = res.getOrNull()
        assertNotNull(creado)
        assertEquals(dto.correo, creado!!.correo)

        coVerify(exactly = 1) { api.crearUsuario(any()) }
    }

    @Test
    fun crear_error_devuelve_failure() = runBlocking {
        val api = mockk<UsuarioApi>()
        coEvery { api.crearUsuario(any()) } throws IOException("fail create")

        val repo = UsuarioRemoteRepository(api)
        val usuario = Usuario(rut = "r", usuario = "u", correo = "c", pass = "p")
        val res = repo.crear(usuario)

        assertTrue(res.isFailure)
        coVerify(exactly = 1) { api.crearUsuario(any()) }
    }
}

