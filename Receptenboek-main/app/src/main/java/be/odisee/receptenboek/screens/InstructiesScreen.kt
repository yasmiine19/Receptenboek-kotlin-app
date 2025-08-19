package be.odisee.receptenboek.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.odisee.receptenboek.Screen
import be.odisee.receptenboek.api.ApiClient
import be.odisee.receptenboek.api.InstructiesApi
import be.odisee.receptenboek.model.Instructie
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructiesScreen(
    navController: NavController,
    gerechtId: Int
) {
    var instructiesLijst by remember { mutableStateOf<List<Instructie>>(emptyList()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var foutmelding by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(gerechtId) {
        scope.launch {
            try {
                val instructiesApi = ApiClient.create(InstructiesApi::class.java)
                val response = instructiesApi.getInstructies(gerechtId)
                instructiesLijst = response.data.filter { it.gerechtId == gerechtId }
                foutmelding = null
            } catch (e: Exception) {
                foutmelding = "Fout bij laden van instructies: ${e.message}"
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFFDF5F0),
        topBar = {
            TopAppBar(
                title = { Text("Instructies", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Terug", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF5BAA41))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (foutmelding != null) {
                Text(foutmelding!!, color = MaterialTheme.colorScheme.error)
            } else if (instructiesLijst.isEmpty()) {
                Text("Geen instructies gevonden.", color = Color(0xFF777777))
            } else {
                val currentStep = instructiesLijst[currentStepIndex]

                // Instructies gecentreerd
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Stap ${currentStep.stapNummer}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF5BAA41)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentStep.stapTekst,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Center
                    )

                }

                // Navigatieknoppen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val buttonColors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5BAA41),
                        contentColor = Color.White
                    )

                    if (currentStepIndex > 0) {
                        Button(
                            onClick = { currentStepIndex-- },
                            colors = buttonColors
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Vorige")
                            Spacer(Modifier.width(8.dp))
                            Text("Vorige")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (currentStepIndex < instructiesLijst.lastIndex) {
                        Button(
                            onClick = { currentStepIndex++ },
                            colors = buttonColors
                        ) {
                            Text("Volgende")
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = "Volgende")
                        }
                    }
                }

                // Sluitknop
                Button(
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BAA41), contentColor = Color.White)
                ) {
                    Text("Sluiten")
                }
            }
        }
    }
}
