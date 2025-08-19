package be.odisee.receptenboek.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.odisee.receptenboek.api.ApiClient
import be.odisee.receptenboek.api.IngredientenApi
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientenScreen(
    navController: NavController,
    gerechtId: Int
) {
    var aantalPersonen by remember { mutableStateOf("4") }
    var ingredientenTekstLijst by remember { mutableStateOf<List<String>>(emptyList()) }
    var foutmelding by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFFDF5F0),
        topBar = {
            TopAppBar(
                title = { Text("Ingrediënten", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Terug", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF5BAA41)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Voor hoeveel personen?", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = aantalPersonen,
                onValueChange = { aantalPersonen = it.filter { c -> c.isDigit() } },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val aantal = aantalPersonen.toIntOrNull() ?: 4
                    scope.launch {
                        try {
                            val ingredientenApi = ApiClient.create(IngredientenApi::class.java)
                            val response = ingredientenApi.getIngredienten(gerechtId)
                            val gefilterde = response.data.filter { it.gerechtId == gerechtId }

                            val berekend = gefilterde.map {
                                val qty = (it.hoeveelheidPer4Pers / 4.0) * aantal
                                val strQty = if (qty % 1.0 == 0.0) qty.toInt().toString() else "%.1f".format(qty)
                                "${it.naam}: $strQty ${it.eenheid}"
                            }

                            ingredientenTekstLijst = berekend
                            foutmelding = null
                        } catch (e: Exception) {
                            foutmelding = "Fout bij laden: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BAA41), contentColor = Color.White)
            ) {
                Text("Toon de ingrediënten")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (foutmelding != null) {
                Text(foutmelding!!, color = MaterialTheme.colorScheme.error)
            }

            if (ingredientenTekstLijst.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ingrediënten:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        ingredientenTekstLijst.forEach {
                            Text("- $it")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("instructies_scherm/$gerechtId") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BAA41), contentColor = Color.White)
                ) {
                    Text("Aan de slag")
                }
            }
        }
    }
}
