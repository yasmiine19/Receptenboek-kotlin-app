package be.odisee.receptenboek.model

data class Favoriet(
    val id: Int,
    val gebruiker_id: Int,
    val gerecht_id: Int,
    val gerecht_naam: String,
    val beschrijving: String,
    val foto_url: String?,
    val bereidingstijd: Int?,
    val moeilijkheidsgraad: String?
)


data class FavorietResponse(
    val data: List<Favoriet>
)
