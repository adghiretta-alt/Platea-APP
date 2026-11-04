package com.kinema.myapplication

data class EntradaCistella (
    val esdevenimentId: String,
    val esdevenimentNom: String,
    val entrada: TipusEntrada,
    var quantitat: Int = 1
)