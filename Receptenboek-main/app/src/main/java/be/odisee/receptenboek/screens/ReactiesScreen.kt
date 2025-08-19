package be.odisee.receptenboek.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.odisee.receptenboek.Screen
import be.odisee.receptenboek.viewmodel.ReactiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactiesScreen(
    gerechtId: Int,
    navController: NavController,
    viewModel: ReactiesViewModel = viewModel()
) {
    val reacties by viewModel.reacties.collectAsState()
    var naam by remember { mutableStateOf("") }
    var tekst by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(3) }
    var showForm by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(gerechtId) {
        viewModel.loadReacties(gerechtId)
    }

    Scaffold(
        containerColor = Color(0xFFFDF5F0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reacties", color = Color.White) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Favorites.route) {
                                popUpTo(0) { inclusive = false }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Terug", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF5BAA41)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(reacties) { reactie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = reactie.gebruiker_naam, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(reactie.score) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = reactie.tekst)
                            Text(
                                text = reactie.datum_toegevoegd,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toggle knop voor formulier
            Button(
                onClick = { showForm = !showForm },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showForm) Color(0xFFF5F0E6) else Color(0xFF5BAA41), // beige bij verbergen, groen bij tonen
                    contentColor = if (showForm) Color(0xFF5BAA41) else Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (showForm) "Verbergen" else "Reactie plaatsen")
                    Icon(
                        imageVector = if (showForm) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = "Toggle form"
                    )
                }
            }

            AnimatedVisibility(
                visible = showForm,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = naam,
                        onValueChange = { naam = it },
                        label = { Text("Je naam") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tekst,
                        onValueChange = { tekst = it },
                        label = { Text("Je reactie") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Score:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..5).forEach { i ->
                            IconButton(onClick = { score = i }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$i sterren",
                                    tint = if (i <= score) Color(0xFFFFC107) else Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (naam.isNotBlank() && tekst.isNotBlank()) {
                                viewModel.postReactie(
                                    gerechtId = gerechtId,
                                    gebruikerNaam = naam,
                                    tekst = tekst,
                                    score = score
                                )
                                naam = ""
                                tekst = ""
                                showForm = false
                                Toast.makeText(context, "Reactie geplaatst!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Vul naam en reactie in.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5BAA41),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Versturen")
                    }
                }
            }
        }
    }
}
