package be.odisee.receptenboek.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import be.odisee.receptenboek.Screen
import be.odisee.receptenboek.api.ApiClient
import be.odisee.receptenboek.api.GebruikerApi
import be.odisee.receptenboek.model.GeplandRecept
import be.odisee.receptenboek.screens.*
import be.odisee.receptenboek.viewmodel.RandomReceptViewModel


@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val gebruikerApi = remember { ApiClient.create(GebruikerApi::class.java) }
    val favViewModel: FavorietenViewModel = viewModel()
    val receptenViewModel: ReceptenViewModel = viewModel()

    // Gerechten laden (één keer)
    LaunchedEffect(Unit) {
        receptenViewModel.loadData()
    }

    // Lokale lijst voor geplande recepten
    val geplandeRecepten = remember { mutableStateListOf<GeplandRecept>() }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val hideBottomBar = currentDestination?.route in listOf("splash", "login", "register")

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                NavigationBar {
                    Screen.bottomItems.forEach { screen ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
           // composable("splash") {
           //     SplashScreen(navController)
           // }

            composable("login") {
                LoginScreen(navController, gebruikerApi)
            }

            composable("register") {
                RegisterScreen(navController, gebruikerApi)
            }

            composable(Screen.Home.route) {
                ReceptenScreen(
                    favViewModel = favViewModel,
                    navController = navController
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    favViewModel = favViewModel,
                    navController = navController
                )
            }


            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }

            composable(
                route = "ingredienten_scherm/{gerechtId}",
                arguments = listOf(navArgument("gerechtId") { type = NavType.IntType })
            ) { backStackEntry ->
                val gerechtId = backStackEntry.arguments?.getInt("gerechtId") ?: 0
                IngredientenScreen(navController = navController, gerechtId = gerechtId)
            }

            composable(
                route = "instructies_scherm/{gerechtId}",
                arguments = listOf(navArgument("gerechtId") { type = NavType.IntType })
            ) { backStackEntry ->
                val gerechtId = backStackEntry.arguments?.getInt("gerechtId") ?: 0
                InstructiesScreen(navController = navController, gerechtId = gerechtId)
            }

            composable(Screen.Agenda.route) {
                AgendaScreen(
                    gerechten = receptenViewModel.gerechten,
                    geplandeRecepten = geplandeRecepten
                )
            }

            composable(
                route = "reacties_scherm/{gerechtId}",
                arguments = listOf(navArgument("gerechtId") { type = NavType.IntType })
            ) { backStackEntry ->
                val gerechtId = backStackEntry.arguments?.getInt("gerechtId") ?: 0
                ReactiesScreen(gerechtId = gerechtId, navController = navController)
            }

            composable(Screen.Inspiratie.route) {
                val inspiratieViewModel: RandomReceptViewModel = viewModel()
                InspiratieScreen(
                    navController = navController,
                    viewModel = inspiratieViewModel
                )
            }



        }
    }
}
