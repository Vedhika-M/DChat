package com.example.dchat

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "loginscreen"
    ){
        composable("loginscreen") {
            LoginScreen(
                navController = navController
            )
        }

        composable("signupscreen"){
            SignupScreen(
                navController = navController
            )
        }

        composable("homescreen") {
            HomeScreen(
                navController = navController
            )
        }

        composable("chatscreen") {
            ChatScreen(
                navController = navController
            )
        }
    }
}