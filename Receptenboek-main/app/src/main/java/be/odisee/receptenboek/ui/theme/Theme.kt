package be.odisee.receptenboek.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 🌿 Basiskleuren geïnspireerd op kruiden & herfst
val PrimaryDarkGreen = Color(0xFF2B4D20)      // Frisdiep groen (voor knoppen & titels)
val ButtonTextOnPrimary = Color(0xFFFFFFFF)   // Wit voor primaire button tekst
val SecondaryGreen = Color(0xFF8AA670)        // Soepel, natuurlijk groen voor secundaire knoppen
val ButtonTextOnSecondary = Color(0xFF2A3B20) // Donkergroen tekst op secundaire knop

// Accent oranje/bruin iets ingetogener en meer earth-tone
val AccentOrange = Color(0xFF9C5B16)          // Dieper warm oranje-bruin (voor tertiary knoppen)
val ButtonTextOnTertiary = Color(0xFF3B1F00)  // Donkerbruine tekst op oranje knop

val SoftOrange = Color(0xFFD1B58E)            // Zacht warme oranje-beige achtergrond
val BackgroundBeige = Color(0xFFFFF8E1)       // Lichtbeige achtergrond

// Cards: licht thema meer beige met hint van oranje-bruin; donker thema iets groener met warme touch
val CardBackgroundLight = Color(0xFFF5EFE0)   // Licht warme beige (met oranje tint)
val CardBackgroundDark = Color(0xFF394729)    // Donker mosgroen met een warme ondertoon

val SoftShadow = Color(0xFF3B2F25)            // Voor tekst op licht

val MutedGreen = Color(0xFF93B27E)            // Iets lichter, vriendelijk groen (secondary)
val HighlightYellow = Color(0xFFBFA15B)       // Mosterdachtig geel, warm en complementair
val LightGreen = Color(0xFFDCE7C8)            // Zacht fris groen voor containers/achtergrond

// 🌙 Donker thema
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkGreen,
    onPrimary = ButtonTextOnPrimary,
    primaryContainer = MutedGreen,
    onPrimaryContainer = Color.White,

    secondary = SecondaryGreen,
    onSecondary = ButtonTextOnSecondary,
    secondaryContainer = Color(0xFF5F7642),
    onSecondaryContainer = Color.White,

    tertiary = AccentOrange,
    onTertiary = ButtonTextOnTertiary,
    tertiaryContainer = Color(0xFF874E10),
    onTertiaryContainer = Color.White,

    background = SoftShadow,
    onBackground = BackgroundBeige,
    surface = CardBackgroundDark,
    onSurface = Color.White,
    surfaceVariant = AccentOrange,
    onSurfaceVariant = Color.White,

    outline = HighlightYellow
)

// ☀️ Licht thema
private val LightColorScheme = lightColorScheme(
    primary = PrimaryDarkGreen,
    onPrimary = ButtonTextOnPrimary,
    primaryContainer = LightGreen,
    onPrimaryContainer = PrimaryDarkGreen,

    secondary = SecondaryGreen,
    onSecondary = ButtonTextOnSecondary,
    secondaryContainer = Color(0xFFC5D6A0),
    onSecondaryContainer = PrimaryDarkGreen,

    tertiary = AccentOrange,
    onTertiary = ButtonTextOnPrimary,
    tertiaryContainer = SoftOrange,
    onTertiaryContainer = AccentOrange,

    background = BackgroundBeige,
    onBackground = PrimaryDarkGreen,
    surface = CardBackgroundLight,
    onSurface = SoftShadow,
    surfaceVariant = SoftOrange,
    onSurfaceVariant = SoftShadow,

    outline = HighlightYellow
)

@Composable
fun ReceptenboekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Forceren eigen stijl
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
