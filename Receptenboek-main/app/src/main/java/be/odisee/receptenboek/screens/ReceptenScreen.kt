package be.odisee.receptenboek.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.odisee.receptenboek.model.Gerecht
import coil.compose.AsyncImage
import androidx.compose.foundation.BorderStroke


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceptenScreen(
    favViewModel: FavorietenViewModel,
    navController: NavController,
    viewModel: ReceptenViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        containerColor = Color(0xFFFDF5F0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Recepten", color = Color.White) },
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
            // Zoekveld
            OutlinedTextField(
                value = viewModel.zoekTerm,
                onValueChange = { viewModel.zoekTerm = it },
                label = { Text("Zoek op naam of beschrijving") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5BAA41),
                    unfocusedBorderColor = Color(0xFFAACFAA),
                    focusedLabelColor = Color(0xFF5BAA41),
                    unfocusedLabelColor = Color(0xFFAACFAA),
                    cursorColor = Color(0xFF5BAA41)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontale filterknoppen
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "Alles"-knop
                item {
                    FilterButton(
                        label = "Alles",
                        selected = viewModel.geselecteerdeCategorie == null,
                        onClick = { viewModel.geselecteerdeCategorie = null }
                    )
                }

                // Dynamische knoppen voor elke categorie
                items(viewModel.categorieen) { categorie ->
                    FilterButton(
                        label = categorie.maaltijd,
                        selected = viewModel.geselecteerdeCategorie == categorie.categorie_id,
                        onClick = { viewModel.geselecteerdeCategorie = categorie.categorie_id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gerechtenlijst
            LazyColumn {
                items(viewModel.gefilterdeGerechten()) { gerecht: Gerecht ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            // FOTO bovenaan
                            gerecht.foto_url?.let { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Foto van ${gerecht.naam}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                )
                            }

                            Column(modifier = Modifier.padding(16.dp)) {
                                // TITEL + FAVORIETEN
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        gerecht.naam ?: "",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color(0xFF333333),
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = {
                                            favViewModel.toggleFavorite(context, gerecht.id)
                                        }
                                    ) {
                                        val filled = favViewModel.isFavorite(gerecht.id)
                                        Icon(
                                            imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = if (filled)
                                                "Verwijder uit favorieten"
                                            else
                                                "Voeg toe aan favorieten",
                                            tint = if (filled) Color(0xFFFFC107) else Color(0xFF5BAA41) // Geel als favoriet
                                        )

                                    }
                                }

                                // BESCHRIJVING
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    gerecht.beschrijving ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666)
                                )

                                // EXTRA INFO
                                Spacer(modifier = Modifier.height(10.dp))
                                Row {
                                    gerecht.bereidingstijd?.let {
                                        Text(
                                            text = "🕒 $it min",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF777777)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    gerecht.moeilijkheidsgraad?.let {
                                        Text(
                                            text = "📊 $it",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF777777)
                                        )
                                    }
                                }


                                // KNOP: Instructies volgen
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        navController.navigate("ingredienten_scherm/${gerecht.id}")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF5BAA41),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Instructies volgen")
                                }

                                 // KNOP: Bekijk reacties (PER RECEPT)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        navController.navigate("reacties_scherm/${gerecht.id}")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF5BAA41)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF5BAA41)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Bekijk reacties")
                                }


                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(label: String, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (selected) Color(0xFF5BAA41) else Color.White
    val contentColor = if (selected) Color.White else Color(0xFF5BAA41)
    val borderColor = if (selected) Color(0xFF5BAA41) else Color(0xFFAACFAA)

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(label)
    }
}
