package com.example.cosa.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // URL base por defecto (FakeStore) - termina con '/'
    private const val DEFAULT_BASE_URL = "https://fakestoreapi.com/"

    // Interceptor para ver las peticiones en Logcat
    private val interceptorLog = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con configuracion de timeouts
    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(interceptorLog)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Crea una instancia de Retrofit para la baseUrl dada
    private fun crearRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Funcion para crear servicios de API; permite pasar una baseUrl personalizada
    fun <T> crearServicio(servicioClase: Class<T>, baseUrl: String = DEFAULT_BASE_URL): T {
        val retrofit = crearRetrofit(baseUrl)
        return retrofit.create(servicioClase)
    }
}
