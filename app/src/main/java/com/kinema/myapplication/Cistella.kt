package com.kinema.myapplication

object Cistella {

    val entrades = mutableListOf<EntradaCistella>()

    fun afegir(esdeveniment: Esdeveniment, entrada: TipusEntrada) {
        afegir(esdeveniment.id, esdeveniment.nom, entrada, mutableListOf())
    }

    fun afegir(
        esdevId: String,
        esdevNom: String,
        entrada: TipusEntrada,
        seients: MutableList<Pair<Int, Int>>
    ) {
        val existent = entrades.find {
            it.esdevenimentId == esdevId && it.entrada.id == entrada.id
        }

        if (existent != null) {
            if (seients.isEmpty()) {
                existent.quantitat++
            } else {
                val nous = seients.filterNot { it in existent.seients }
                existent.seients.addAll(nous)
                if (existent.seients.isNotEmpty()) {
                    existent.quantitat = existent.seients.size
                }
            }
        } else {
            entrades.add(
                EntradaCistella(
                    esdevenimentId = esdevId,
                    esdevenimentNom = esdevNom,
                    entrada = entrada,
                    quantitat = if (seients.isEmpty()) 1 else seients.size,
                    seients = seients.toMutableList()
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