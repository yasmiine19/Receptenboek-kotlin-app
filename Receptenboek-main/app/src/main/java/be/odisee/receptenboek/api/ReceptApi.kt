package be.odisee.receptenboek.api

import be.odisee.receptenboek.model.BasicResponse
import be.odisee.receptenboek.model.Categorie
import be.odisee.receptenboek.model.Gerecht
import retrofit2.http.GET

interface ReceptApi {

    @GET("gerechten/get.php")
    suspend fun getGerechten(): BasicResponse<List<Gerecht>>

    @GET("categorieen/get.php")
    suspend fun getCategorieen(): BasicResponse<List<Categorie>>
}
