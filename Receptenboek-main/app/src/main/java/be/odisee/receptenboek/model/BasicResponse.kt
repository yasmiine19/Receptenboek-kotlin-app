package be.odisee.receptenboek.model

data class BasicResponse<T>(
    val code: Int,
    val status: String,
    val message: String? = null,
    val data: T? = null
)


