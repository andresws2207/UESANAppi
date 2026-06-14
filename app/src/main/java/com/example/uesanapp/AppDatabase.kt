package com.example.uesanapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.uesanapp.data.DataBase.Pais
import com.example.uesanapp.data.DataBase.PaisDAO
import com.example.uesanapp.data.DataBase.PaisFavorito
import com.example.uesanapp.data.DataBase.Usuario
import com.example.uesanapp.data.DataBase.UsuarioDAO

@Database(entities = [Usuario::class, Pais::class, PaisFavorito::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDAO
    abstract fun paisDao(): PaisDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uesan_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}