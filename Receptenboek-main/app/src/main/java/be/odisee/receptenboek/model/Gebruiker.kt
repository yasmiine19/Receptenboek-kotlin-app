package be.odisee.receptenboek.model

data class Gebruiker(
    val ID: Int,
    val NAME: String,
    val email: String?,
    val gsm: String?,
    val foto: String?
)


data class LoginRequest(val name: String, val password: String)
data class RegisterRequest(val name: String, val password: String)
data class LoginResponse(val code: Int, val message: String, val data: Gebruiker?)
data class RegisterResponse(val code: Int, val message: String)
