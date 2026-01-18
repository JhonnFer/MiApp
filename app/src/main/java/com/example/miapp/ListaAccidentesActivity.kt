package com.example.miapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miapp.ui.theme.MiAppTheme
import com.example.miapp.models.Accidente
import com.example.miapp.repository.AccidenteRepository
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment




class ListaAccidentesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiAppTheme {
                ListaAccidentesScreen()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaAccidentesScreen() {

    // Leer lista desde el repositorio
    val accidentes = remember {
        AccidenteRepository.accidentes

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accidentes Registrados") }
            )
        }
    ) { padding ->

        if (accidentes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center

            ) {
                Text("No hay accidentes registrados")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(accidentes) { accidente ->
                    AccidenteItem(accidente)
                }
            }
        }
    }
}

@Composable
fun AccidenteItem(accidente: Accidente) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Tipo: ${accidente.tipo}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Fecha: ${accidente.fecha}")
            Text("Placa: ${accidente.placa}")
            Text("Conductor: ${accidente.nombreConductor}")
            Text("Cédula: ${accidente.cedula}")
            Text("Observaciones: ${accidente.observaciones}")

            if (accidente.latitud != null && accidente.longitud != null) {
                Text(
                    text = "Ubicación: ${accidente.latitud}, ${accidente.longitud}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
