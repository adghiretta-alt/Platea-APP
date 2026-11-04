package com.kinema.myapplication

object Cistella {

    val entrades = mutableListOf<EntradaCistella>()

    fun afegir(esdeveniment: Esdeveniment, entrada: TipusEntrada) {

        val existent = entrades.find {
            it.esdevenimentId == esdeveniment.id &&
                    it.entrada.id == entrada.id
        }

        if (existent != null) {
            existent.quantitat++
        } else {
            entrades.add(
                EntradaCistella(
                    esdevenimentId = esdeveniment.id,
                    esdevenimentNom = esdeveniment.nom,
                    entrada = entrada
                )
            )
        }
    }

    fun totalElements(): Int {
        return entrades.sumOf { it.quantitat }
    }

    fun calcularTotal(): Double {
        return entrades.sumOf {
            it.entrada.preu * it.quantitat
        }
    }
}