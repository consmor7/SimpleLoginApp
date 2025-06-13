package com.example.mysimpleloginapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

/**
 * Pantalla de bienvenida después del login.
 *
 * @param userEmail Correo electrónico del usuario autenticado.
 * @param onLogout Acción que se ejecuta cuando el usuario presiona "Cerrar sesión".
 */
@Composable
fun HomeScreen(userEmail: String, onLogout: () -> Unit) {

    // Obtén una instancia de Firestore
    val firestore = Firebase.firestore
    // Obtén el UID del usuario actual
    val uid = FirebaseAuth.getInstance().currentUser?.uid


    //Estados de los datos personales del usuario
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var datosGuardados by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }

    //Cargamos los datos del usuario desde Firestore si es que existen
    LaunchedEffect(uid) {
        uid?.let { //Si el UID no es nulo, cargamos los datos del usuario
            firestore.collection("usuarios").document(it).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) { //Si el documento existe, cargamos los datos
                        nombre = doc.getString("nombre") ?: ""
                        edad = doc.getLong("edad")?.toString() ?: ""
                        ciudad = doc.getString("ciudad") ?: ""
                        datosGuardados = true
                    }
                    cargando = false //Cuando cargue los datos, cargando pasa a false
                }
                .addOnFailureListener {//Si falla, cargando pasa a false
                    cargando = false //Cuando cargue los datos, cargando pasa a false
                }
        }
    }

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
        if (!datosGuardados) {
            //Formulario para capturar los datos
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = { Text("Ciudad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val datos = hashMapOf(
                    "nombre" to nombre,
                    "edad" to edad.toIntOrNull(),
                    "ciudad" to ciudad
                )
                uid?.let {
                    firestore.collection("usuarios").document(it).set(datos)
                        .addOnSuccessListener { datosGuardados = true }
                }
            }) {
                Text("Guardar datos")
            }
        } else {
            // Mostrar datos guardados
            Text("Datos personales guardados:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nombre: $nombre")
            Text("Edad: $edad")
            Text("Ciudad: $ciudad")
        }

        //Separador entre campos de 32
        Spacer(modifier = Modifier.height(32.dp))

        // 🔓 Botón para cerrar sesión
        Button(onClick = onLogout) {
            Text("Cerrar sesión")
        }
    }
}


