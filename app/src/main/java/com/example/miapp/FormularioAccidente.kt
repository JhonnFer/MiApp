package com.example.miapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.util.*
import com.example.miapp.models.Accidente
import com.example.miapp.repository.AccidenteRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioAccidente() {

    val context = LocalContext.current

    // ---------- Estados ----------
    var tipoAccidente by remember { mutableStateOf("Choque") }
    var fecha by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }

    // ---------- Cámara ----------
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uriString = result.data?.getStringExtra("IMAGE_URI")
            imageUri = uriString?.let { Uri.parse(it) }
        }
    }

    // ---------- Ubicación ----------
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            latitud = result.data?.getDoubleExtra("LAT", 0.0)
            longitud = result.data?.getDoubleExtra("LON", 0.0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Registro de Accidente",
            style = MaterialTheme.typography.headlineSmall
        )

        // ---------- Tipo de Accidente ----------
        DropdownMenuBox(
            selected = tipoAccidente,
            options = listOf("Choque", "Colisión", "Atropello"),
            onSelectionChange = { tipoAccidente = it }
        )

        // ---------- Fecha ----------
        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha") },
            trailingIcon = {
                IconButton(onClick = {
                    val c = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d -> fecha = "$d/${m + 1}/$y" },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = placa,
            onValueChange = { placa = it },
            label = { Text("Placa") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Conductor") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cedula,
            onValueChange = { cedula = it },
            label = { Text("Cédula") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = { Text("Observaciones") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // ---------- Foto ----------
        Button(
            onClick = {
                cameraLauncher.launch(Intent(context, CameraActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tomar Fotografía")
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        // ---------- Ubicación ----------
        Button(
            onClick = {
                locationLauncher.launch(Intent(context, LocationActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Obtener Ubicación GPS")
        }

        if (latitud != null && longitud != null) {
            Text("Latitud: $latitud")
            Text("Longitud: $longitud")
        }

        // ---------- GUARDAR ----------
        Button(
            onClick = {

                val accidente = Accidente(
                    tipo = tipoAccidente,
                    fecha = fecha,
                    placa = placa,
                    nombreConductor = nombre,
                    cedula = cedula,
                    observaciones = observaciones,
                    fotoUri = imageUri,
                    latitud = latitud,
                    longitud = longitud
                )

                AccidenteRepository.accidentes.add(accidente)

                val vibrator = context.getSystemService(Vibrator::class.java)
                val pattern = longArrayOf(
                    0,    // sin espera inicial
                    500,  // vibra
                    500,  // pausa
                    500,  // vibra
                    500,  // pausa
                    500,  // vibra
                    500,  // pausa
                    500,  // vibra
                    500,  // pausa
                    500   // vibra
                )
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(pattern, -1)
                )


                (context as Activity).finish()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Accidente")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    selected: String,
    options: List<String>,
    onSelectionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de Accidente") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectionChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
