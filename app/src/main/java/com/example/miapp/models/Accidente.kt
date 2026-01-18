package com.example.miapp.models

import android.net.Uri

data class Accidente(
    val tipo: String,
    val fecha: String,
    val placa: String,
    val nombreConductor: String,
    val cedula: String,
    val observaciones: String,
    val fotoUri: Uri?,
    val latitud: Double?,
    val longitud: Double?
)
