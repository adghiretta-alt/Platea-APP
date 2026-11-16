package com.kinema.myapplication

data class EntradaCistella (
    val esdevenimentId: String,
    val esdevenimentNom: String,
    val entrada: TipusEntrada,
    var quantitat: Int = 1,
    val seients: MutableList<Pair<Int, Int>> = mutableListOf(),
    var error: Boolean = false
) {
    val seientLabel: String
        get() = if (seients.isEmpty()) "General"
        else seients.joinToString(", ") { "F${it.first}-N${it.second}" }
}