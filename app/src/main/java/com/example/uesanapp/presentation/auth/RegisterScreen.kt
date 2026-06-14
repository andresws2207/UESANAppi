package com.example.uesanapp.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.uesanapp.data.remote.FirebaseAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.uesanapp.AppDatabase
import com.example.uesanapp.data.DataBase.Usuario
import com.example.uesanapp.data.UserSession

@Composable
fun RegisterScreen(navController: NavController){
    var email by remember {mutableStateOf("")}
    var name by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}
    var confirmPassword by remember {mutableStateOf("")}

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    //room
    val database = remember { AppDatabase.getDatabase(context) }
    val usuarioDAO = database.usuarioDao()

    Column(
        modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .statusBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text("Registro de usuario",
            style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = {name = it},
            label = { Text("Nombre")},
            placeholder = { Text("Nombre")},
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text("Correo")},
            placeholder = { Text("Correo")},
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("Contraseña")},
            placeholder = { Text("Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {confirmPassword = it},
            label = { Text("Confirmar Contraseña")},
            placeholder = { Text("Confirmar Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick={
                if(email.isNotBlank()
                    && password.isNotBlank()
                    && password == confirmPassword)
                {
                    if (password.length < 6) {
                        Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "Correo inválido", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val usuarioExistente = usuarioDAO.obtenerUsuarioPorEmail(email)

                            if(usuarioExistente != null){
                                withContext(Dispatchers.Main){
                                    Toast.makeText(context, "Este correo ya está registrado", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                //crear objeto usuario
                                val nuevoUsuario = Usuario(
                                    email = email,
                                    password = password,
                                    name = name,
                                    avatar = null,
                                    createdAt = System.currentTimeMillis().toString()
                                )
                                val generatedId = usuarioDAO.registrarUsuario(nuevoUsuario) //guardar en SQLite
                                UserSession.userId = generatedId.toInt()
                                
                                withContext(Dispatchers.Main){
                                    Toast.makeText(context, "Usuario registrado localmente", Toast.LENGTH_SHORT).show()
                                    navController.navigate("home/${generatedId.toInt()}"){
                                        popUpTo("register"){ inclusive = true }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Por favor, valida todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Registrar")
        }
        Text(
            text = "Ya tienes cuenta? Inicia Sesión",
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    navController.navigate("login")
                }
        )
    }
}