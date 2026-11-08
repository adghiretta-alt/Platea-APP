package com.kinema.myapplication

import org.json.JSONObject

object JSONParser {

    fun parseEsdeveniments(jsonObj: JSONObject): List<Esdeveniment> {
        val llista = mutableListOf<Esdeveniment>()

        if (!jsonObj.has("esdeveniments")) return llista

        val esdevsObj = jsonObj.getJSONObject("esdeveniments")
        val keys = esdevsObj.keys()

        while (keys.hasNext()) {
            val key = keys.next()
            val item = esdevsObj.getJSONObject(key)

            val entradesList = mutableListOf<TipusEntrada>()
            val entradesArray = item.getJSONArray("tipusEntrades")

            for (i in 0 until entradesArray.length()) {
                val eObj = entradesArray.getJSONObject(i)
                entradesList.add(
                    TipusEntrada(
                        id = eObj.optString("id"),
                        nom = eObj.optString("nom"),
                        zona = eObj.optString("zona"),
                        tipoZona = eObj.optString("tipoZona"),
                        preu = eObj.optDouble("preu", 0.0),
                        quota = eObj.optInt("quota", 0)
                    )
                )
            }

            val esdeveniment = Esdeveniment(
                id = item.optString("id"),
                nom = item.optString("nom"),
                dataHora = item.optString("dataHora"),
                categoria = item.optString("categoria"),
                descripcio = item.optString("descripcio"),
                imatge = item.optString("imatge"),
                recinte = item.optString("recinte"),
                tipusEntrades = entradesList
            )

            llista.add(esdeveniment)
        }

        return llista
    }
}