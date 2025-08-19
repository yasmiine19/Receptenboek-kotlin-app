package be.odisee.receptenboek.model


import com.google.gson.annotations.SerializedName

data class Ingredient(
    val id: Int,

    @SerializedName("gerecht_id")
    val gerechtId: Int,

    @SerializedName("ingredient")
    val naam: String,

    @SerializedName("hoeveelheid_per_4_pers")
    val hoeveelheidPer4Pers: Int,

    val eenheid: String,

    var hoeveelheid: String? = null
)
