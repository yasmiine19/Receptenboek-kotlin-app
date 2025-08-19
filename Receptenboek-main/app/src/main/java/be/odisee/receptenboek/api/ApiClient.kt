package be.odisee.receptenboek.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL =
    "https://tesniemallali.be/receptAPI/api/inc/"

private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object ApiClient {
    fun <T> create(service: Class<T>): T =
        retrofit.create(service)
}
