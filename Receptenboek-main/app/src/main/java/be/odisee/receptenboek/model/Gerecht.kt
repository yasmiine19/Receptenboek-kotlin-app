package be.odisee.receptenboek.model

data class Gerecht(
    val id: Int,
    val naam: String,
    val categorie_id: Int,
    val beschrijving: String,
    val bereidingstijd: Int?,
    val moeilijkheidsgraad: String?,
    val foto_url: String?
)


data class GerechtResponse(
    val data: List<Gerecht>
)
