package be.odisee.receptenboek.api

import be.odisee.receptenboek.model.Ingredient
import be.odisee.receptenboek.model.IngredientResponse
import be.odisee.receptenboek.model.ListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IngredientenApi {
    @GET("ingredienten/get.php")
    suspend fun getIngredienten(@Query("gerecht_id") gerechtId: Int
    ): IngredientResponse
}
