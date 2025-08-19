package be.odisee.receptenboek.api

import be.odisee.receptenboek.model.ListResponse
import be.odisee.receptenboek.model.Instructie
import retrofit2.http.GET
import retrofit2.http.Query

interface InstructiesApi {
    @GET("instructies/get.php")
    suspend fun getInstructies(
        @Query("gerecht_id") gerechtId: Int
    ): ListResponse<Instructie>
}
