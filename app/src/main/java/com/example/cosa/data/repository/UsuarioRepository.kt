package com.example.cosa.data.repository

import com.example.cosa.data.dao.UsuarioDAO
import com.example.cosa.data.model.Usuario

class UsuarioRepository(
    private val usuarioDAO: UsuarioDAO,
    private val remoteRepo: UsuarioRemoteRepository? = null
) {
    // Registrar: intenta crear en remoto si existe el remoteRepo, si falla usa local
    suspend fun registrar(usuario: Usuario) {
        if (remoteRepo != null) {
            try {
                val resultado = remoteRepo.crear(usuario)
                if (resultado.isSuccess) {
                    val creado = resultado.getOrNull()!!
                    usuarioDAO.insert(creado)
                    return
                }
            } catch (_: Exception) {
                // ignore and fallback to local
            }
        }

        usuarioDAO.insert(usuario)
    }

    suspend fun existeCorreo(correo: String): Boolean {
        return usuarioDAO.getUsuarioPorCorreo(correo) != null
    }

    suspend fun existeUsuario(usuario: String): Boolean {
        return usuarioDAO.getUsuarioPorNombre(usuario) != null
    }

    // Obtener por correo: primero local, si no, intentar remoto y cachear
    suspend fun obtenerPorCorreo(correo: String): Usuario? {
        val local = usuarioDAO.getUsuarioPorCorreo(correo)
        if (local != null) return local

        if (remoteRepo != null) {
            try {
                val res = remoteRepo.obtenerTodos()
                if (res.isSuccess) {
                    val lista = res.getOrNull() ?: emptyList()
                    val encontrado = lista.firstOrNull { it.correo == correo }
                    if (encontrado != null) {
                        // cachear en DB local
                        usuarioDAO.insert(encontrado)
                        return encontrado
                    }
                }
            } catch (_: Exception) {
                // fallback: null
            }
        }

        return null
    }

    // Actualizar: intenta remoto si tiene id válido, sino solo local
    suspend fun actualizarUsuario(usuario: Usuario) {
        if (remoteRepo != null && usuario.id > 0) {
            try {
                val res = remoteRepo.actualizar(usuario.id, usuario)
                if (res.isSuccess) {
                    val actualizado = res.getOrNull()!!
                    usuarioDAO.update(actualizado)
                    return
                }
            } catch (_: Exception) {
                // ignore and fallback
            }
        }

        usuarioDAO.update(usuario)
    }

    // Eliminar por id (opcional)
    suspend fun eliminarPorId(id: Int) {
        if (remoteRepo != null && id > 0) {
            try {
                val res = remoteRepo.eliminar(id)
                if (res.isSuccess) {
                    // Eliminar local si existe una entrada con ese id
                    usuarioDAO.deleteById(id)
                    return
                }
            } catch (_: Exception) {
                // fallback: intentar eliminar local
            }
        }

        usuarioDAO.deleteById(id)
    }

    // Obtener por id (helper)
    suspend fun obtenerPorId(id: Int): Usuario? {
        return usuarioDAO.getAll().firstOrNull { it.id == id }
    }
}