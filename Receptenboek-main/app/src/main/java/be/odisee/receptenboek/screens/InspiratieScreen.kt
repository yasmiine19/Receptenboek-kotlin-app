package be.odisee.receptenboek.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.odisee.receptenboek.viewmodel.RandomReceptViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspiratieScreen(navController: NavController, viewModel: RandomReceptViewModel = viewModel()) {
    val recept by viewModel.recept.collectAsState()
    var ingrediëntenInput by remember { mutableStateOf("") }
    var categorieInput by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categorieën = listOf(
        "1" to "Ontbijt",
        "2" to "Lunch",
        "3" to "Dinner",
        "4" to "Dessert"
    )

    Scaffold(
        containerColor = Color(0xFFFDF5F0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inspiratie", color = Color.White) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { viewModel.haalVolledigRandomReceptOp() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BAA41), contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎲 Verras me volledig")
            }

            OutlinedTextField(
                value = ingrediëntenInput,
                onValueChange = { ingrediëntenInput = it },
                label = { Text("Ingrediënten (bv. kip, rijst)") },
                modifier = Modifier.fillMaxWidth()
            )

            // DROPDOWN VOOR CATEGORIE
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categorieën.find { it.first == categorieInput }?.second ?: "Selecteer categorie",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categorie") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorieën.forEach { (id, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                categorieInput = id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.haalMatchendReceptOp(ingrediëntenInput, categorieInput)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF5BAA41)),
                border = ButtonDefaults.outlinedButtonBorder,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🧂 Verras me op basis van voorraad")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // RESULTAAT TONEN
            if (recept == null) {
                Text("Nog geen recept opgehaald.", color = Color.Gray)
            } else {
                Text("🎉 ${recept!!.naam}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(recept!!.beschrijving, style = MaterialTheme.typography.bodyMedium)
                recept!!.bereidingstijd?.let { tijd ->
                    Text("🕒 $tijd minuten", style = MaterialTheme.typography.labelMedium)
                }
                recept!!.moeilijkheidsgraad?.let { moeilijk ->
                    Text("📊 Moeilijkheid: $moeilijk", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
