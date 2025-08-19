package be.odisee.receptenboek.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.odisee.receptenboek.api.GebruikerApi
import be.odisee.receptenboek.model.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, api: GebruikerApi) {
    var gebruikersnaam by remember { mutableStateOf("") }
    var wachtwoord by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5F0))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Account aanmaken",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF5BAA41)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = gebruikersnaam,
                onValueChange = { gebruikersnaam = it },
                label = { Text("Gebruikersnaam") },
                modifier = Modifier.fillMaxWidth(),
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
                        if (gebruikersnaam.isBlank() || wachtwoord.isBlank()) {
                            feedback = "Velden mogen niet leeg zijn."
                            success = false
                            return@launch
                        }

                        try {
                            val response = api.register(RegisterRequest(gebruikersnaam, wachtwoord))
                            if (response.code == 200) {
                                success = true
                                feedback = "✅ Account succesvol aangemaakt!"
                            } else {
                                success = false
                                feedback = response.message
                            }
                        } catch (e: Exception) {
                            success = false
                            feedback = "❌ Fout: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5BAA41),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Registreren")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (feedback.isNotEmpty()) {
                Text(
                    text = feedback,
                    color = if (success) Color(0xFF5BAA41) else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("← Terug naar login", color = Color(0xFF5BAA41))
            }
        }
    }
}
