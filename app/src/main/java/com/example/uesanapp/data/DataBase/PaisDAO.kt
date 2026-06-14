package com.example.uesanapp.data.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface PaisDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPais(pais: Pais): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarAFavoritos(favorito: PaisFavorito)

    @Query("SELECT * FROM paises WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerPaisPorNombre(nombre: String): Pais?

    @Query("SELECT * FROM paises_favoritos WHERE user_id = :userId AND pais_id = :paisId LIMIT 1")
    suspend fun esFavorito(userId: Int, paisId: Int): PaisFavorito?

    @Delete
    suspend fun eliminarDeFavoritos(favorito: PaisFavorito)
    
    @Query("DELETE FROM paises_favoritos WHERE user_id = :userId AND pais_id = :paisId")
    suspend fun eliminarFavoritoPorIds(userId: Int, paisId: Int)

    @Query("SELECT p.* FROM paises p INNER JOIN paises_favoritos f ON p.id = f.pais_id WHERE f.user_id = :userId")
    suspend fun obtenerPaisesFavoritos(userId: Int): List<Pais>
}
