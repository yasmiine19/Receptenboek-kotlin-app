package be.odisee.receptenboek.api

import be.odisee.receptenboek.model.FavorietResponse
import be.odisee.receptenboek.model.BasicResponse
import retrofit2.http.*

interface FavorietApi {

    @GET("favorieten/get.php")
    suspend fun getFavorites(
        @Query("gebruiker_id") gebruikerId: Int
    ): FavorietResponse

    @FormUrlEncoded
    @POST("favorieten/add.php")
    suspend fun addFavorite(
        @Field("gebruiker_id") gebruikerId: Int,
        @Field("gerecht_id") gerechtId: Int
    ): BasicResponse<Unit>


    @FormUrlEncoded
    @POST("favorieten/delete.php")
    suspend fun deleteFavorite(
        @Field("gebruiker_id") gebruikerId: Int,
        @Field("gerecht_id") gerechtId: Int
    ): BasicResponse<Unit>

}
