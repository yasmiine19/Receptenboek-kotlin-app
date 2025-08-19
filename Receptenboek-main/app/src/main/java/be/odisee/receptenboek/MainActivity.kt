package be.odisee.receptenboek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import be.odisee.receptenboek.api.ApiClient
import be.odisee.receptenboek.api.GebruikerApi
import be.odisee.receptenboek.model.Gerecht
import be.odisee.receptenboek.screens.*
import be.odisee.receptenboek.ui.theme.ReceptenboekTheme
import kotlinx.coroutines.delay
import be.odisee.receptenboek.navigation.AppNavigator
import androidx.lifecycle.viewmodel.compose.viewModel
import be.odisee.receptenboek.screens.ReceptenViewModel




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReceptenboekTheme(
                darkTheme = false, // Of true als je donker thema wil forceren
                dynamicColor = false // Belangrijk: gebruik je eigen kleuren!
            ) {
                AppNavigator()
            }
        }
    }
}


@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        // Simuleer laadduur
        delay(2500)
        navController.navigate(Screen.Home.route) {
            popUpTo(0) { inclusive = true } // Splash verwijderen uit stack
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.boek),
            contentDescription = "Splash background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAAFFFFFF))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Receptenboek", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}


@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val gebruikerApi = remember { ApiClient.create(GebruikerApi::class.java) }

    /** ⭐ gedeeld Favorieten‑VM */
    val favViewModel: FavorietenViewModel = viewModel()

    /** huidige route voor selectie‑state */
    val currentRoute by navController.currentBackStackEntryAsState()
    val selected = currentRoute?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                Screen.bottomItems.forEach { screen ->
                    NavigationBarItem(
                        selected = selected == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { inner ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(inner)
        ) {
            composable("login")    { LoginScreen(navController, gebruikerApi) }
            composable("register") { RegisterScreen(navController, gebruikerApi) }

            composable(Screen.Home.route) {
                ReceptenScreen(favViewModel = favViewModel)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(favViewModel = favViewModel, navController = navController)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceptenScreen(
    favViewModel: FavorietenViewModel,
    viewModel: ReceptenViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.loadData() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Recepten",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(viewModel.gerechten) { gerecht: Gerecht ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                gerecht.naam,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                gerecht.beschrijving,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
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
                                tint = MaterialTheme.colorScheme.tertiary // AccentOrange
                            )
                        }
                    }
                }
            }
        }
    }
}
