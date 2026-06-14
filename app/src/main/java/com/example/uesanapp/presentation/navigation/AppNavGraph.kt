package com.example.uesanapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uesanapp.data.UserSession
import com.example.uesanapp.presentation.apifootball.ApiFootballScreen
import com.example.uesanapp.presentation.auth.LoginScreen
import com.example.uesanapp.presentation.auth.RegisterScreen
import com.example.uesanapp.presentation.home.FavoritosScreen
import com.example.uesanapp.presentation.home.HomeScreen
import com.example.uesanapp.presentation.permissions.GalleryPermissionsScreen

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    NavHost(navController = navController,
            startDestination = "register"){
        composable("register"){ RegisterScreen(navController) }
        composable("login"){ LoginScreen(navController) }
        composable("home/{userId}",
            listOf(navArgument("userId"){ NavType.IntType })
        ){ backStackEntry ->
            val argId = backStackEntry.arguments?.getInt("userId") ?: 0
            val userId = if (argId > 0) argId else UserSession.userId
            
            DrawerScaffold(navController = navController, userId = userId) {
                HomeScreen(userId = userId)
            }
        }
        composable("permissions"){
            DrawerScaffold(navController) {
                GalleryPermissionsScreen()
            }
        }
        composable("football"){
            DrawerScaffold(navController) {
                ApiFootballScreen()
            }
        }
        composable("favorites/{userId}",
            listOf(
                navArgument("userId"){ NavType.IntType}
            )){ backStackEntry ->
            val argId = backStackEntry.arguments?.getInt("userId") ?: 0
            val userId = if (argId > 0) argId else UserSession.userId

            DrawerScaffold(navController = navController, userId = userId) {
                FavoritosScreen(navController = navController, userId = userId)
            }
        }
    }
}