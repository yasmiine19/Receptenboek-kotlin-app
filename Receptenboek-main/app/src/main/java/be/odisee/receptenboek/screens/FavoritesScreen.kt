package be.odisee.receptenboek.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavController
import be.odisee.receptenboek.model.Favoriet
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favViewModel: FavorietenViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { favViewModel.loadFavorites(context) }

    Scaffold(
        containerColor = Color(0xFFFDF5F0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mijn favorieten", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF5BAA41)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (favViewModel.favorieten.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Geen favorieten gevonden",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF777777)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favViewModel.favorieten) { fav: Favoriet ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                fav.foto_url?.let { url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Foto van ${fav.gerecht_naam}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                    )
                                }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            fav.gerecht_naam,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color(0xFF333333),
                                            modifier = Modifier.weight(1f)
                                        )

                                        IconButton(
                                            onClick = {
                                                favViewModel.toggleFavorite(context, fav.gerecht_id)
                                            }
                                        ) {
                                            val isFav = favViewModel.isFavorite(fav.gerecht_id)
                                            Icon(
                                                imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.Star,
                                                contentDescription = if (isFav)
                                                    "Verwijder uit favorieten"
                                                else
                                                    "Voeg toe aan favorieten",
                                                tint = if (isFav) Color(0xFFFFC107) else Color(0xFF5BAA41)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        fav.beschrijving,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF666666)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row {
                                        fav.bereidingstijd?.let {
                                            Text(
                                                text = "🕒 $it min",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF777777)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        fav.moeilijkheidsgraad?.let {
                                            Text(
                                                text = "📊 $it",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF777777)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            navController.navigate("ingredienten_scherm/${fav.gerecht_id}")
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

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            navController.navigate("reacties_scherm/${fav.gerecht_id}")
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
}
