package be.odisee.receptenboek.api

import be.odisee.receptenboek.model.Reactie
import retrofit2.http.*

data class ReactieRequest(
    val gerecht_id: Int,
    val gebruiker_naam: String,
    val tekst: String,
    val score: Int
)

data class ReactieResponse(
    val code: Int,
    val message: String
)

data class ReactieListResponse(
    val code: Int,
    val data: List<Reactie>
)

interface ReactieApi {
    @GET("reacties/get.php")
    suspend fun getReacties(@Query("gerecht_id") gerechtId: Int): ReactieListResponse

    @POST("reacties/add.php")
    suspend fun postReactie(@Body reactie: ReactieRequest): ReactieResponse
}
