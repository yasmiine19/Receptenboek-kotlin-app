package be.odisee.receptenboek.model

import com.google.gson.annotations.SerializedName

data class Instructie(
    val id: Int,

    @SerializedName("gerecht_id")
    val gerechtId: Int,

    @SerializedName("stap_nummer")
    val stapNummer: Int,

    @SerializedName("stap_tekst")
    val stapTekst: String
)
