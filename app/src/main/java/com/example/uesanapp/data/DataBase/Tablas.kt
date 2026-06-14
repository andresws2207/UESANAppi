package com.example.uesanapp.data.DataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String = "",
    val password: String = "",
    @ColumnInfo(name = "user_name")
    val name: String = "",
    val avatar: String? = null, // Puede ser un String nulo si no tiene foto
    @ColumnInfo(name = "created_at")
    val createdAt: String = ""
)

@Entity(tableName = "paises")
data class Pais(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String = "",
    @ColumnInfo(name = "codigo_iso")
    val codigoIso: String = "" // Ej: "MX", "AR", "ES"
)

@Entity(
    tableName = "paises_favoritos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Pais::class,
            parentColumns = ["id"],
            childColumns = ["pais_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PaisFavorito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id", index = true)
    val userId: Int = 0,

    @ColumnInfo(name = "pais_id", index = true)
    val paisId: Int = 0
)