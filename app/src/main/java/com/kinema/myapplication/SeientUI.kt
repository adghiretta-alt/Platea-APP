package com.kinema.myapplication

class SeientUI {
    data class Casella(
        val zonaId: String,
        val fila: Int,
        val numero: Int,
        val estat: String
    ) {
        val key: String get() = "$zonaId-$fila-$numero"
    }
}