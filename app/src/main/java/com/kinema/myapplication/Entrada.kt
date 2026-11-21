package com.kinema.myapplication

import android.R

data class Entrada(
    val esdeveniment: String,
    val tipusEntrada: String,
    val titular: String,
    val fila: String?,
    val numero: String?,
    val tipusZona: String,
    val localitzador: String
)