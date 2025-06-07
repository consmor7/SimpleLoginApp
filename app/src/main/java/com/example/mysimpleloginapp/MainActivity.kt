package com.example.mysimpleloginapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.example.mysimpleloginapp.LoginScreen


class MainActivity : ComponentActivity() {
    // 🔐 FirebaseAuth se declara a nivel de clase para usarlo en todo el Activity
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Inicializa Firebase para que funcione en la app
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // 🧱 Jetpack Compose UI
        setContent {
            // 🚦 Controlador de navegación para movernos entre pantallas
            val navController = rememberNavController()

            // 🔁 Declaramos las pantallas disponibles en la app
            NavHost(navController, startDestination = "login") {

                // 📍 Pantalla de Login
                composable("login") {
                    LoginScreen(
                        onLoginClick = { email, password ->
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this@MainActivity) { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@MainActivity, "Login exitoso", Toast.LENGTH_SHORT).show()
                                        navController.navigate("home/${email}") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(this@MainActivity, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        },
                        onNavigateToRegister = {
                            navController.navigate("register")
                        }
                    )


                }
                // 📍 Pantalla de Registro
                composable("register") {
                    RegisterScreen(
                        onRegister = { email, password, confirm ->
                            if (password != confirm) {
                                Toast.makeText(this@MainActivity, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                return@RegisterScreen
                            }

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this@MainActivity) { task ->
                                    if (task.isSuccessful) {
                                        navController.navigate("home/${email}") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(this@MainActivity, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        },
                        onNavigateToLogin = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }


                // 📍 Pantalla de Bienvenida (Home)
                composable("home/{email}") { backStackEntry ->
                    // 📨 Extraemos el email desde la ruta de navegación
                    val email = backStackEntry.arguments?.getString("email") ?: ""

                    HomeScreen(userEmail = email) {
                        // 🔓 Cierre de sesión
                        auth.signOut()

                        // ↩️ Navegar de vuelta al login (limpia el stack)
                        navController.navigate("login") {
                            popUpTo("home/{email}") { inclusive = true }
                        }
                    }
                }

            }
        }
    }
}
