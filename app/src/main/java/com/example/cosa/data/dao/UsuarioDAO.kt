package com.example.cosa.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cosa.data.model.Usuario

@Dao
interface UsuarioDAO {
    @Insert
    suspend fun insert(usuario: Usuario)

    @Update
    suspend fun update(usuario: Usuario)
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getUsuarioPorCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE nombre = :usuario LIMIT 1")
    suspend fun getUsuarioPorNombre(usuario: String): Usuario?

    @Query("SELECT * FROM usuarios")
    suspend fun getAll(): List<Usuario>

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE usuarios SET rol = :rut, nombre = :usuario, correo = :correo, pass = :pass WHERE id = :id")
    suspend fun updateById(id: Int, rut: String, usuario: String, correo: String, pass: String)



}