package be.odisee.receptenboek.api

import be.odisee.receptenboek.model.*
import be.odisee.receptenboek.model.Gerecht
import be.odisee.receptenboek.model.Categorie
import be.odisee.receptenboek.model.BasicResponse

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part



interface GebruikerApi {
    @POST("gebruikers/post.php")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("gebruikers/add.php")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("categorieen/get.php")
    suspend fun getCategorieen(): BasicResponse<List<Categorie>>

    @GET("gerechten/get.php")
    suspend fun getGerechten(): BasicResponse<List<Gerecht>>

    @POST("gebruikers/get.php")
    suspend fun getGebruikerInfo(@Body body: Map<String, Int>): LoginResponse

    @POST("gebruikers/update.php")
    suspend fun updateGebruiker(@Body data: Map<String, String>): BasicResponse<Unit>

    @Multipart
    @POST("gebruikers/upload_foto.php")
    suspend fun uploadFoto(@Part foto: MultipartBody.Part): BasicResponse<String>



}



