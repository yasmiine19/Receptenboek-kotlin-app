package be.odisee.receptenboek.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.odisee.receptenboek.api.GebruikerApi
import be.odisee.receptenboek.datastore.GebruikerManager
import be.odisee.receptenboek.model.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    navController: NavController,
    api: GebruikerApi
) {
    val context = LocalContext.current
    var gebruikersnaam by remember { mutableStateOf("") }
    var wachtwoord by remember { mutableStateOf("") }
    var foutmelding by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5F0)) // Soft cream achtergrond
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welkom terug !",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF5BAA41) // Primary green
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = gebruikersnaam,
                onValueChange = { gebruikersnaam = it },
                label = { Text("Gebruikersnaam") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5BAA41),
                    unfocusedBorderColor = Color(0xFFAACFAA),
                    focusedLabelColor = Color(0xFF5BAA41),
                    unfocusedLabelColor = Color(0xFFAACFAA),
                    cursorColor = Color(0xFF5BAA41)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = wachtwoord,
                onValueChange = { wachtwoord = it },
                label = { Text("Wachtwoord") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5BAA41),
                    unfocusedBorderColor = Color(0xFFAACFAA),
                    focusedLabelColor = Color(0xFF5BAA41),
                    unfocusedLabelColor = Color(0xFFAACFAA),
                    cursorColor = Color(0xFF5BAA41)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val response = api.login(LoginRequest(gebruikersnaam, wachtwoord))
                            if (response.code == 200 && response.data != null) {
                                GebruikerManager.saveUserId(context, response.data.ID)
                                withContext(Dispatchers.Main) {
                                    navController.navigate("home")
                                }
                            } else {
                                foutmelding = response.message
                            }
                        } catch (e: Exception) {
                            foutmelding = "Fout: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5BAA41),
                    contentColor = Color.White
                )
            ) {
                Text("Inloggen")
            }

            if (foutmelding.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = foutmelding,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text(
                    "Nog geen account? Registreer hier",
                    color = Color(0xFFF4A261) // Accent orange
                )
            }
        }
    }
}
