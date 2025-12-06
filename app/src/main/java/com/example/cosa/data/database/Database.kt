package com.example.cosa.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cosa.data.dao.UsuarioDAO
import com.example.cosa.data.model.Usuario

@Database(entities = [Usuario::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // si ya existe la instancia, la devuelve; sino la crea
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-db" // nombre del archivo de la DB
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
