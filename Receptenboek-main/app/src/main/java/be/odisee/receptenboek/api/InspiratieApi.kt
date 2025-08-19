package be.odisee.receptenboek.api

import be.odisee.receptenboek.model.InspiratieResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class MatchRequest(val ingrediënten: String, val categorie: String)

interface InspiratieApi {

    @GET("inspiratie/random.php")
    suspend fun getRandomRecept(): InspiratieResponse

    @POST("inspiratie/match.php") // ← hier dus het juiste pad
    suspend fun getMatchedRecept(@Body request: MatchRequest): InspiratieResponse
}
