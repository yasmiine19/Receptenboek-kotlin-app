package be.odisee.receptenboek

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.TipsAndUpdates




sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Recepten", Icons.Default.Home)
    object Favorites : Screen("favorites", "Favorieten", Icons.Filled.Star)
    object Profile : Screen("profile", "Profiel", Icons.Default.Person)
    object Agenda : Screen("agenda", "Agenda", Icons.Default.DateRange)
    object Inspiratie : Screen("inspiratie", "Inspiratie", Icons.Default.TipsAndUpdates)



    companion object {
        val bottomItems = listOf(Home, Favorites, Profile, Agenda, Inspiratie)
    }

}



