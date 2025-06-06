package com.example.mysimpleloginapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Pantalla de bienvenida después del login.
 *
 * @param userEmail Correo electrónico del usuario autenticado.
 * @param onLogout Acción que se ejecuta cuando el usuario presiona "Cerrar sesión".
 */
@Composable
fun HomeScreen(userEmail: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🟢 Mensaje de bienvenida
        Text("¡Bienvenido!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // 📧 Mostrar email del usuario autenticado
        Text("Sesión activa como:")
        Text(userEmail, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // 🔓 Botón para cerrar sesión
        Button(onClick = onLogout) {
            Text("Cerrar sesión")
        }
    }
}
//Ejemplo de una vista previa
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        //Muestra un ejemplo solo para el preview
        HomeScreen("william.henry.harrison@example-pet-store.com", {})
    }
}

