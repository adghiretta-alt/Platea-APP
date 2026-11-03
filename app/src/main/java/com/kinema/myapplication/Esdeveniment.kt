package com.kinema.myapplication

data class Esdeveniment(
    val id: String,
    val nom: String,
    val dataHora: String,
    val categoria: String,
    val descripcio: String,
    val imatge: String,
    val recinte: String,
    val tipusEntrades: List<TipusEntrada>
)