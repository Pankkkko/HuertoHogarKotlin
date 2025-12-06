package com.example.cosa.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val TAG = "RetrofitClient"

    // URL base por defecto (FakeStore) - termina con '/'
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"

    // Interceptor para ver las peticiones en Logcat (solo si detectamos DEBUG)
    private val interceptorLog: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // Comprueba vía reflexión si BuildConfig.DEBUG está disponible y es true
    private fun isDebugBuild(): Boolean {
        return try {
            val cls = Class.forName("com.example.cosa.BuildConfig")
            val field = cls.getField("DEBUG")
            field.getBoolean(null)
        } catch (ex: Exception) {
            false
        }
    }

    // Cliente HTTP con configuracion de timeouts, agrega interceptor solo si es debug
    private val clienteHttp: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (isDebugBuild()) {
            try {
                builder.addInterceptor(interceptorLog)
            } catch (ignored: Exception) {
                // no-op
            }
        }

        builder.build()
    }

    // Crea una instancia de Retrofit para la baseUrl dada
    private fun crearRetrofit(baseUrl: String): Retrofit {
        Log.d(TAG, "Creando Retrofit con baseUrl=$baseUrl")
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
