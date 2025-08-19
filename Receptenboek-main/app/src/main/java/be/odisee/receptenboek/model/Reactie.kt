package be.odisee.receptenboek.model

data class Reactie(
    val gebruiker_naam: String,
    val tekst: String,
    val score: Int,
    val datum_toegevoegd: String
)
