package be.odisee.receptenboek.model

import java.time.LocalDate
import java.time.LocalTime

data class GeplandRecept(
    val naam: String,
    val datum: LocalDate,
    val tijd: LocalTime
)
