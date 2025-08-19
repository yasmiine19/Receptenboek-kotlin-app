package be.odisee.receptenboek.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import be.odisee.receptenboek.model.Gerecht
import be.odisee.receptenboek.model.GeplandRecept

@SuppressLint("NewApi")
@Composable
fun ReceptPlanDialoog(
    datum: LocalDate,
    onPlan: (GeplandRecept) -> Unit,
    onDismiss: () -> Unit,
    beschikbareGerechten: List<Gerecht>
) {
    var geselecteerdGerecht by remember { mutableStateOf<Gerecht?>(null) }
    var tijdInput by remember { mutableStateOf("12:00") } // HH:mm format

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    // Parse tijdInput naar LocalTime
                    val tijd = try {
                        LocalTime.parse(tijdInput, DateTimeFormatter.ofPattern("HH:mm"))
                    } catch (e: Exception) {
                        LocalTime.NOON // fallback tijd
                    }
                    geselecteerdGerecht?.let {
                        onPlan(GeplandRecept(it.naam, datum, tijd))
                    }
                },
                enabled = geselecteerdGerecht != null
            ) {
                Text("Plan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuleer")
            }
        },
        title = { Text("Plan een Recept voor ${datum.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}") },
        text = {
            Column {
                // Dropdown menu gerechten
                var expanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedTextField(
                        value = geselecteerdGerecht?.naam ?: "Selecteer gerecht",
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                        label = { Text("Gerecht") }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        beschikbareGerechten.forEach { gerecht ->
                            DropdownMenuItem(
                                text = { Text(gerecht.naam) },
                                onClick = {
                                    geselecteerdGerecht = gerecht
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tijd input (HH:mm)
                OutlinedTextField(
                    value = tijdInput,
                    onValueChange = { tijdInput = it },
                    label = { Text("Tijd (HH:mm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
