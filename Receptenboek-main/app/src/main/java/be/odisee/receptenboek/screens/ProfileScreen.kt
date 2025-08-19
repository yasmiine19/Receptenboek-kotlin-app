package be.odisee.receptenboek.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: GebruikerViewModel = viewModel()
    val gebruiker by viewModel.gebruiker.collectAsState()

    var email by remember { mutableStateOf("") }
    var gsm by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf("") }
    var wachtwoord by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var bewerken by remember { mutableStateOf(false) }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fotoUri = uri
        foto = ""
    }

    LaunchedEffect(Unit) {
        viewModel.haalGebruikerOp(context)
    }

    LaunchedEffect(gebruiker) {
        gebruiker?.let {
            email = it.email ?: ""
            gsm = it.gsm ?: ""
            foto = it.foto ?: ""
        }
    }

    Scaffold(
        containerColor = Color(0xFFFDF5F0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mijn profiel", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF5BAA41)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            if (gebruiker != null) {
                if (fotoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(fotoUri),
                        contentDescription = "gekozen foto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                    )
                } else if (foto.isNotBlank()) {
                    AsyncImage(
                        model = foto,
                        contentDescription = "bestaande foto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                    )
                }

                if (bewerken) {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { launcher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BAA41))
                    ) {
                        Text("Kies nieuwe foto", color = Color.White)
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    singleLine = true,
                    enabled = bewerken,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = gsm,
                    onValueChange = { gsm = it },
                    label = { Text("GSM") },
                    singleLine = true,
                    enabled = bewerken,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = wachtwoord,
                    onValueChange = { wachtwoord = it },
                    label = { Text("Nieuw wachtwoord") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = bewerken,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                if (!bewerken) {
                    Button(
                        onClick = { bewerken = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Wijzig", color = Color.White)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (fotoUri != null) {
                                    viewModel.uploadFoto(context, fotoUri!!) { uploadedUrl ->
                                        foto = uploadedUrl
                                        viewModel.updateGebruiker(
                                            context, email, gsm, foto, wachtwoord.ifBlank { null },
                                            onSuccess = {
                                                feedback = "✅ Foto & profiel geüpdatet"
                                                bewerken = false
                                            },
                                            onError = { feedback = "❌ $it" }
                                        )
                                    }
                                } else {
                                    viewModel.updateGebruiker(
                                        context, email, gsm, foto, wachtwoord.ifBlank { null },
                                        onSuccess = {
                                            feedback = "✅ Profiel geüpdatet"
                                            bewerken = false
                                        },
                                        onError = { feedback = "❌ $it" }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BAA41)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Opslaan", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                gebruiker?.let {
                                    email = it.email ?: ""
                                    gsm = it.gsm ?: ""
                                    foto = it.foto ?: ""
                                }
                                wachtwoord = ""
                                fotoUri = null
                                bewerken = false
                                feedback = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Annuleer")
                        }
                    }
                }

                if (feedback.isNotBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text(feedback, color = Color(0xFF5BAA41))
                }

                Spacer(Modifier.height(32.dp))
            } else {
                CircularProgressIndicator()
                Spacer(Modifier.height(24.dp))
            }

            Button(
                onClick = {
                    navController.navigate("login") { popUpTo(0) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BAA41))
            ) {
                Text("Uitloggen", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
