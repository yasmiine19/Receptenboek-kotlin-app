package be.odisee.receptenboek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.odisee.receptenboek.api.ApiClient
import be.odisee.receptenboek.api.InspiratieApi
import be.odisee.receptenboek.api.MatchRequest
import be.odisee.receptenboek.model.Gerecht
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RandomReceptViewModel : ViewModel() {

    private val api = ApiClient.create(InspiratieApi::class.java)

    private val _recept = MutableStateFlow<Gerecht?>(null)
    val recept: StateFlow<Gerecht?> = _recept

    fun haalVolledigRandomReceptOp() {
        viewModelScope.launch {
            try {
                val response = api.getRandomRecept()
                if (response.code == 200) {
                    _recept.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun haalMatchendReceptOp(ingrediënten: String, categorie: String) {
        viewModelScope.launch {
            try {
                val request = MatchRequest(ingrediënten = ingrediënten, categorie = categorie)
                val response = api.getMatchedRecept(request)
                if (response.code == 200) {
                    _recept.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
