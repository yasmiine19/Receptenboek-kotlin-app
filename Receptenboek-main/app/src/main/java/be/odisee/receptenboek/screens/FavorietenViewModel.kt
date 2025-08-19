package be.odisee.receptenboek.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.odisee.receptenboek.api.ApiClient
import be.odisee.receptenboek.api.FavorietApi
import be.odisee.receptenboek.datastore.GebruikerManager
import be.odisee.receptenboek.model.Favoriet
import kotlinx.coroutines.launch
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*



class FavorietenViewModel : ViewModel() {

    private val api = ApiClient.create(FavorietApi::class.java)
    var favorieten = mutableStateListOf<Favoriet>()
        private set

    fun loadFavorites(context: android.content.Context) {
        viewModelScope.launch {
            val userId = GebruikerManager.getUserId(context) ?: return@launch
            try {
                val response = api.getFavorites(userId)
                favorieten.clear()
                favorieten.addAll(response.data)
            } catch (e: Exception) {
                println("❌ Fout bij laden van favorieten: ${e.message}")
            }
        }
    }
    fun isFavorite(gerechtId: Int) = favorieten.any { it.gerecht_id == gerechtId }

    fun toggleFavorite(context: Context, gerechtId: Int) {
        viewModelScope.launch {
            val userId = GebruikerManager.getUserId(context) ?: return@launch
            try {
                if (isFavorite(gerechtId)) {
                    api.deleteFavorite(userId, gerechtId)
                    favorieten.removeAll { it.gerecht_id == gerechtId }
                } else {
                    api.addFavorite(userId, gerechtId)

                    favorieten.add(
                        Favoriet(
                            id = 0,
                            gebruiker_id = userId,
                            gerecht_id = gerechtId,
                            gerecht_naam = "",
                            beschrijving = "",
                            foto_url = null,
                            bereidingstijd = null,
                            moeilijkheidsgraad = null
                        )
                    )

                }
            } catch (e: Exception) {
                println("❌ Toggle favoriet mislukt: ${e.message}")
            }
        }
    }
}
