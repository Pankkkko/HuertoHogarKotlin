package com.example.cosa.data

import com.example.cosa.data.remote.RetrofitClient
import com.example.cosa.data.remote.api.ProductoApi
import com.example.cosa.data.remote.api.UsuarioApi
import com.example.cosa.data.repository.ProductoRemoteRepository
import com.example.cosa.data.repository.ProductoRepository
import com.example.cosa.data.repository.UsuarioRemoteRepository
import com.example.cosa.data.repository.UsuarioRepository
import com.example.cosa.data.database.AppDatabase
import android.content.Context
import androidx.room.Room

object ServiceLocator {

    // Crea el API de usuario usando una baseUrl personalizada (por ejemplo: https://mi-servidor/huertohogar/api/)
    fun createUsuarioApi(baseUrl: String): UsuarioApi {
        return RetrofitClient.crearServicio(UsuarioApi::class.java, baseUrl)
    }

    // Sobrecarga que mantiene la compatibilidad (usa DEFAULT_BASE_URL)
    fun createUsuarioApi(): UsuarioApi {
        return RetrofitClient.crearServicio(UsuarioApi::class.java)
    }

    fun createProductoApi(baseUrl: String): ProductoApi {
        return RetrofitClient.crearServicio(ProductoApi::class.java, baseUrl)
    }

    // Sobrecarga que mantiene la compatibilidad (usa DEFAULT_BASE_URL)
    fun createProductoApi(): ProductoApi {
        return RetrofitClient.crearServicio(ProductoApi::class.java)
    }

    // Crea repositorios (requiere contexto para DB)
    fun createUsuarioRepository(context: Context, usuarioBaseUrl: String? = null): UsuarioRepository {
        val db = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app-db")
            .fallbackToDestructiveMigration()
            .build()

        val usuarioDao = db.usuarioDao()
        val api = if (usuarioBaseUrl != null) createUsuarioApi(usuarioBaseUrl) else createUsuarioApi()
        val remote = UsuarioRemoteRepository(api)
        return UsuarioRepository(usuarioDao, remote)
    }

    fun createProductoRepository(baseUrl: String? = null): ProductoRepository {
        val api = if (baseUrl != null) createProductoApi(baseUrl) else createProductoApi()
        val remote = ProductoRemoteRepository(api)
        return ProductoRepository(remote)
    }
}
