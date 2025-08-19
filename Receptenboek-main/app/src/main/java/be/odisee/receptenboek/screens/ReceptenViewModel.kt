package be.odisee.receptenboek.screens

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.odisee.receptenboek.api.ApiClient
import be.odisee.receptenboek.api.ReceptApi
import be.odisee.receptenboek.model.Gerecht
import kotlinx.coroutines.launch
import be.odisee.receptenboek.model.Categorie

class ReceptenViewModel : ViewModel() {

    private val api = ApiClient.create(ReceptApi::class.java)

    var gerechten by mutableStateOf<List<Gerecht>>(emptyList())
        private set

    var categorieen by mutableStateOf<List<Categorie>>(emptyList())
        private set

    var zoekTerm by mutableStateOf("")
    var geselecteerdeCategorie by mutableStateOf<Int?>(null)

    fun loadData() {
        viewModelScope.launch {
            try {
                val resGerechten = api.getGerechten()
                val resCategorieen = api.getCategorieen()
                gerechten = resGerechten.data ?: emptyList()
                categorieen = resCategorieen.data ?: emptyList()
            } catch (e: Exception) {
                println("❌ Fout bij laden: ${e.message}")
            }
        }
    }

    fun gefilterdeGerechten(): List<Gerecht> {
        val term = zoekTerm.lowercase().trim()
        return gerechten.filter { gerecht ->
            (term.isEmpty() || gerecht.naam.lowercase().contains(term) || gerecht.beschrijving.lowercase().contains(term)) &&
                    (geselecteerdeCategorie == null || gerecht.categorie_id == geselecteerdeCategorie)
        }
    }

    // (Optioneel) resetknopje
    fun resetFilters() {
        zoekTerm = ""
        geselecteerdeCategorie = null
    }
}
