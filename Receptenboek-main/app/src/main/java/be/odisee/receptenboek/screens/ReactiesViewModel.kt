package be.odisee.receptenboek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.odisee.receptenboek.api.ApiClient
import be.odisee.receptenboek.api.ReactieApi
import be.odisee.receptenboek.api.ReactieRequest
import be.odisee.receptenboek.model.Reactie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch




class ReactiesViewModel : ViewModel() {

    private val api = ApiClient.create(ReactieApi::class.java)

    private val _reacties = MutableStateFlow<List<Reactie>>(emptyList())
    val reacties: StateFlow<List<Reactie>> get() = _reacties

    fun loadReacties(gerechtId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getReacties(gerechtId)
                if (response.code == 200) {
                    _reacties.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postReactie(gerechtId: Int, gebruikerNaam: String, tekst: String, score: Int) {
        viewModelScope.launch {
            try {
                val reactie = ReactieRequest(gerecht_id = gerechtId, gebruiker_naam = gebruikerNaam, tekst = tekst, score = score)
                val response = api.postReactie(reactie)
                if (response.code == 200) {
                    loadReacties(gerechtId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
