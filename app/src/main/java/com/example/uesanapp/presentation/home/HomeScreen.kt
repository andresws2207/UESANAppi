package com.example.uesanapp.presentation.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.uesanapp.AppDatabase
import com.example.uesanapp.data.DataBase.Pais
import com.example.uesanapp.data.DataBase.PaisFavorito
import com.example.uesanapp.data.UserSession
import com.example.uesanapp.data.model.CountryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val mockCountries = listOf(
    CountryModel("Colombia", 5, "https://flagcdn.com/w320/co.png"),
    CountryModel("Francia", 3, "https://flagcdn.com/w320/fr.png"),
    CountryModel("Brasil", 8, "https://flagcdn.com/w320/br.png"),
    CountryModel("España", 2, "https://flagcdn.com/w320/es.png"),
    CountryModel("Portugal", 7, "https://flagcdn.com/w320/pt.png"),
    CountryModel("Argentina", 1, "https://flagcdn.com/w320/ar.png"),
    CountryModel("Japón", 10, "https://flagcdn.com/w320/jp.png"),
    CountryModel("Perú", 50, "https://flagcdn.com/w320/pe.png"),
)

@Composable
fun HomeScreen(userId: Int){
    // Si el userId es inválido, intentamos recuperarlo de la sesión global
    val effectiveUserId = if (userId > 0) userId else UserSession.userId
    
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val paisDao = database.paisDao()
    val coroutineScope = rememberCoroutineScope()
    
    // Estado para rastrear favoritos (mapa de nombre de país a booleano)
    var favoriteStates by remember { mutableStateOf(mapOf<String, Boolean>()) }
    
    // Cargar favoritos iniciales
    LaunchedEffect(effectiveUserId) {
        coroutineScope.launch(Dispatchers.IO) {
            val states = mutableMapOf<String, Boolean>()
            mockCountries.forEach { country ->
                val dbPais = paisDao.obtenerPaisPorNombre(country.name)
                if (dbPais != null) {
                    val isFav = paisDao.esFavorito(effectiveUserId, dbPais.id) != null
                    states[country.name] = isFav
                } else {
                    states[country.name] = false
                }
            }
            withContext(Dispatchers.Main) {
                favoriteStates = states
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ranking FIFA 2026", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(mockCountries){ country ->
                val isFavorite = favoriteStates[country.name] ?: false
                
                Card(
                    modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            contentDescription = country.name,
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop,
                            painter = rememberAsyncImagePainter(country.imageUrl)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(country.name, style = MaterialTheme.typography.titleMedium)
                            Text("Ranking FIFA 2026: ${country.ranking}" )
                        }
                        
                        IconButton(onClick = {
                            if (effectiveUserId <= 0) {
                                Toast.makeText(context, "Error: Usuario no identificado. Por favor, re-inicia sesión.", Toast.LENGTH_LONG).show()
                                return@IconButton
                            }
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    // 1. Asegurar que el país existe en la tabla 'paises'
                                    var dbPais = paisDao.obtenerPaisPorNombre(country.name)
                                    if (dbPais == null) {
                                        paisDao.insertarPais(Pais(nombre = country.name, codigoIso = ""))
                                        dbPais = paisDao.obtenerPaisPorNombre(country.name)
                                    }

                                    if (dbPais != null) {
                                        if (isFavorite) {
                                            paisDao.eliminarFavoritoPorIds(effectiveUserId, dbPais.id)
                                        } else {
                                            paisDao.agregarAFavoritos(PaisFavorito(userId = effectiveUserId, paisId = dbPais.id))
                                        }

                                        withContext(Dispatchers.Main) {
                                            favoriteStates = favoriteStates.toMutableMap().apply {
                                                put(country.name, !isFavorite)
                                            }
                                            val message = if (!isFavorite) "Añadido a favoritos" else "Eliminado de favoritos"
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error: No se pudo verificar el país en la base de datos.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) Color.Red else Color.Gray
                            )
                        }
                    }

                }
            }
        }

    }
}
