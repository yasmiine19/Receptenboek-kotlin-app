package be.odisee.receptenboek.screens

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.odisee.receptenboek.api.ApiClient
import be.odisee.receptenboek.api.GebruikerApi
import be.odisee.receptenboek.datastore.GebruikerManager
import be.odisee.receptenboek.model.Gebruiker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class GebruikerViewModel : ViewModel() {

    private val api = ApiClient.create(GebruikerApi::class.java)
    private val _gebruiker = MutableStateFlow<Gebruiker?>(null)
    val gebruiker: StateFlow<Gebruiker?> = _gebruiker

    // ✅ Gebruiker ophalen
    fun haalGebruikerOp(context: Context) {
        viewModelScope.launch {
            try {
                val id = GebruikerManager.getUserId(context)
                if (id != null) {
                    val response = api.getGebruikerInfo(mapOf("user_id" to id))
                    if (response.code == 200) {
                        _gebruiker.value = response.data
                    }
                }
            } catch (e: Exception) {
                println("❌ Gebruiker ophalen mislukt: ${e.message}")
            }
        }
    }

    // ✅ Gebruikergegevens bijwerken
    fun updateGebruiker(
        context: Context,
        email: String?,
        gsm: String?,
        foto: String?,
        nieuwWachtwoord: String?,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = GebruikerManager.getUserId(context)
            if (id == null) {
                onError("Gebruiker-ID niet gevonden.")
                return@launch
            }

            val data = mutableMapOf<String, String>()
            data["user_id"] = id.toString()
            if (!email.isNullOrBlank()) data["email"] = email
            if (!gsm.isNullOrBlank()) data["gsm"] = gsm
            if (!foto.isNullOrBlank()) data["foto"] = foto
            if (!nieuwWachtwoord.isNullOrBlank()) data["new_password"] = nieuwWachtwoord

            try {
                val response = api.updateGebruiker(data)
                if (response.code == 200) {
                    haalGebruikerOp(context) // opnieuw ophalen na update
                    onSuccess()
                } else {
                    onError("Foutmelding: ${response.message}")
                }
            } catch (e: Exception) {
                onError("Netwerkfout: ${e.message}")
            }
        }
    }

    // ✅ Profielfoto uploaden
    fun uploadFoto(context: Context, uri: Uri, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri) ?: return@launch
                val bytes = inputStream.readBytes()
                val requestBody = bytes.toRequestBody("image/*".toMediaType())
                val fotoPart = MultipartBody.Part.createFormData("foto", "profielfoto.jpg", requestBody)

                val response = api.uploadFoto(fotoPart)
                if (response.code == 200 && !response.message.isNullOrBlank()) {
                    onSuccess(response.message)
                } else {
                    println("❌ Upload fout: ${response.message}")
                }
            } catch (e: Exception) {
                println("❌ Foto uploaden mislukt: ${e.message}")
            }
        }
    }
}
