package com.example.uesanapp.presentation.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.uesanapp.AppDatabase
import com.example.uesanapp.data.DataBase.Pais
import com.example.uesanapp.data.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FavoritosScreen(navController: NavController, userId: Int) {
    val effectiveUserId = if (userId > 0) userId else UserSession.userId
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Instancia de la Base de Datos
    val database = remember { AppDatabase.getDatabase(context) }
    val paisDao = database.paisDao() // Asegúrate de que este método existe en tu AppDatabase

    // Estado para almacenar la lista de países favoritos
    var listaFavoritos by remember { mutableStateOf<List<Pais>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // LaunchedEffect ejecuta la carga de datos de manera asíncrona al entrar a la pantalla
    LaunchedEffect(key1 = effectiveUserId) {
        withContext(Dispatchers.IO) {
            try {
                // Opcional: Descomenta la línea de abajo si quieres insertar datos de prueba la primera vez
                // pf_insertarDatosPrueba(paisDao, effectiveUserId)

                val favoritos = paisDao.obtenerPaisesFavoritos(effectiveUserId)
                withContext(Dispatchers.Main) {
                    listaFavoritos = favoritos
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al cargar favoritos: ${e.message}", Toast.LENGTH_LONG).show()
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Text(
            text = "Mis Países Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando favoritos...")
            }
        } else if (listaFavoritos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Aún no tienes países favoritos agregados.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            // Listado de favoritos
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listaFavoritos) { pais ->
                    PaisFavoritoItem(
                        pais = pais,
                        onEliminarClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    // Eliminamos de la base de datos usando el método de tu DAO
                                    paisDao.eliminarFavoritoPorIds(effectiveUserId, pais.id)

                                    // Volvemos a consultar la lista actualizada para refrescar la UI
                                    val favoritosActualizados = paisDao.obtenerPaisesFavoritos(effectiveUserId)

                                    withContext(Dispatchers.Main) {
                                        listaFavoritos = favoritosActualizados
                                        Toast.makeText(context, "${pais.nombre} eliminado de favoritos", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "No se pudo eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PaisFavoritoItem(pais: Pais, onEliminarClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = pais.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Código ISO: ${pais.codigoIso}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Botón con ícono de basurero para eliminar el favorito
            IconButton(onClick = onEliminarClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar de favoritos",
                    tint = Color.Red
                )
            }
        }
    }
}