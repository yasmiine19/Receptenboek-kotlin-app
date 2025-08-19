package be.odisee.receptenboek.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.odisee.receptenboek.model.GeplandRecept
import be.odisee.receptenboek.model.Gerecht
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

@SuppressLint("NewApi")
@Composable
fun AgendaScreen(
    gerechten: List<Gerecht>,
    geplandeRecepten: MutableList<GeplandRecept>
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5F0))
            .padding(16.dp)
    ) {
        Column {
            // Titel
            Text(
                text = "Mijn agenda",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF5BAA41),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Maand + navigatie
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Vorige maand")
                }
                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Volgende maand")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weekdagen
            val daysOfWeek = DayOfWeek.values()
            Row(modifier = Modifier.fillMaxWidth()) {
                for (day in daysOfWeek) {
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Kalender
            val firstDayOfMonth = currentMonth.atDay(1)
            val lastDayOfMonth = currentMonth.atEndOfMonth()
            val firstDayOfWeekIndex = firstDayOfMonth.dayOfWeek.ordinal
            val totalDays = lastDayOfMonth.dayOfMonth
            val totalCells = ((firstDayOfWeekIndex + totalDays + 6) / 7) * 7

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(300.dp),
                userScrollEnabled = false
            ) {
                // Lege cellen
                items(firstDayOfWeekIndex) {
                    Box(modifier = Modifier.size(40.dp))
                }

                // Dagen
                items(totalDays) { dayIndex ->
                    val date = currentMonth.atDay(dayIndex + 1)
                    val isSelected = date == selectedDate
                    val isToday = date == LocalDate.now()
                    val hasRecipe = geplandeRecepten.any { it.datum == date }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                when {
                                    isSelected -> Color(0xFF5BAA41)
                                    hasRecipe -> Color(0xFF5BAA41).copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { selectedDate = date },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (dayIndex + 1).toString(),
                            color = if (isSelected) Color.White else Color.Black,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Lege cellen na de maand
                items(totalCells - (firstDayOfWeekIndex + totalDays)) {
                    Box(modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Plan-knop
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BAA41))
            ) {
                Text("Plan Recept", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lijst geplande recepten voor geselecteerde dag
            Text(
                "Geplande recepten voor ${selectedDate.dayOfMonth}-${selectedDate.monthValue}-${selectedDate.year}:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            val receptenVoorDatum = geplandeRecepten.filter { it.datum == selectedDate }
            if (receptenVoorDatum.isEmpty()) {
                Text("Geen geplande recepten")
            } else {
                receptenVoorDatum.forEach {
                    Text("🍽️ ${it.naam} om ${it.tijd}")
                }
            }

            // Dialoog tonen
            if (showDialog) {
                ReceptPlanDialoog(
                    datum = selectedDate,
                    beschikbareGerechten = gerechten,
                    onPlan = {
                        geplandeRecepten.add(it)
                        // Voeg toe aan echte agenda van de gsm
                        planReceptInAgenda(
                            context = context,
                            receptNaam = it.naam,
                            datum = it.datum,
                            tijd = it.tijd.toString()
                        )
                        showDialog = false
                    },
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

// Deze functie opent de Agenda van de gebruiker met een ingevulde gebeurtenis
@SuppressLint("NewApi")
fun planReceptInAgenda(context: Context, receptNaam: String, datum: LocalDate, tijd: String) {
    val tijdParts = tijd.split(":")
    val uur = tijdParts[0].toIntOrNull() ?: 12
    val minuut = tijdParts.getOrNull(1)?.toIntOrNull() ?: 0

    val startMillis = datum
        .atTime(uur, minuut)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, receptNaam)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        putExtra(CalendarContract.Events.DESCRIPTION, "Gepland recept uit de Receptenboek app")
    }

    context.startActivity(intent)
}
