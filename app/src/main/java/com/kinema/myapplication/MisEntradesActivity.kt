package com.kinema.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class MisEntradesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_entrades)

        // Obtenemos los elementos de la pantalla.
        val recycler = findViewById<RecyclerView>(R.id.recyclerEntrades)

        // Configuramos la lista de entradas.
        recycler.layoutManager = LinearLayoutManager(this)

        // Recuperamos la respuesta enviada desde CartelleraActivity.
        val jsonStr = intent.getStringExtra("JSON_ENTRADES")

        if (jsonStr != null) {

            // Convertimos la respuesta en un objeto JSON.
            val resposta = JSONObject(jsonStr)

            if (resposta.optString("status") == "OK") {

                val entradesJSON = resposta.getJSONArray("entrades")
                val llistaEntrades = mutableListOf<Entrada>()

                // Recorremos las entradas recibidas.
                for (i in 0 until entradesJSON.length()) {

                    val entrada = entradesJSON.getJSONObject(i)

                    // Recuperamos la fila y el número si existen.
                    val filaJSON = entrada.opt("fila")
                    val numeroJSON = entrada.opt("numero")

                    val fila = if (filaJSON == null || filaJSON == JSONObject.NULL) {
                        null
                    } else {
                        filaJSON.toString()
                    }

                    val numero = if (numeroJSON == null || numeroJSON == JSONObject.NULL) {
                        null
                    } else {
                        numeroJSON.toString()
                    }

                    // Creamos la entrada.
                    val novaEntrada = Entrada(
                        esdeveniment = entrada.optString("esdeveniment"),
                        tipusEntrada = entrada.optString("tipusEntrada"),
                        titular = entrada.optString("titular"),
                        fila = fila,
                        numero = numero,
                        tipusZona = entrada.optString("tipusZona")
                    )

                    llistaEntrades.add(novaEntrada)
                }

                // Mostramos un mensaje si no hay entradas.
                if (llistaEntrades.isEmpty()) {
                    val tvBuit = TextView(this)
                    tvBuit.text = "No tens entrades comprades."
                    tvBuit.textSize = 18f
                    recycler.visibility = RecyclerView.GONE

                    // Añadimos el mensaje a la pantalla.
                    val contenidor = findViewById<android.view.ViewGroup>(R.id.contenidorEntrades)
                    contenidor.addView(tvBuit)
                } else {
                    // Configuramos el adaptador.
                    recycler.adapter = EntradesAdapter(llistaEntrades)
                }

            } else {
                recycler.visibility = RecyclerView.GONE

                val tvError = TextView(this)
                tvError.text = "No s'han pogut carregar les teves entrades."
                tvError.textSize = 18f

                val contenidor = findViewById<android.view.ViewGroup>(R.id.contenidorEntrades)
                contenidor.addView(tvError)
            }
        }
    }
}