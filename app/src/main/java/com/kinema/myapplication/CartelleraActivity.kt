package com.kinema.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import android.content.Intent

class CartelleraActivity : AppCompatActivity() {

    // Variable que muestra el número de entradas de la cesta
    private lateinit var tvCistella: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cartellera)

        // Obtenemos los elementos de la pantalla
        val recycler = findViewById<RecyclerView>(R.id.recyclerEvents)
        tvCistella = findViewById(R.id.tvCistella)

        // Botón para consultar las entradas compradas.
        val btnMisEntrades = findViewById<Button>(R.id.btnMisEntrades)

        btnMisEntrades.setOnClickListener {

            // Comprobar que tenemos conexión con el servidor.
            if (WebSocketManager.client != null && WebSocketManager.client!!.isOpen) {

                // Preparar la recepción de las entradas.
                WebSocketManager.respuestaEntrades = { respuesta ->

                    val intent = Intent(this, MisEntradesActivity::class.java)
                    intent.putExtra("JSON_ENTRADES", respuesta)
                    startActivity(intent)

                    // Limpiar la respuesta para no reutilizarla.
                    WebSocketManager.respuestaEntrades = null
                }

                // Preparar la petición al servidor.
                val request = JSONObject()
                request.put("type", "myTickets")
                request.put("correo", WebSocketManager.correo)

                // Enviar la petición.
                WebSocketManager.client!!.send(request.toString())

            } else {
                Toast.makeText(this, "No hay conexión con el servidor", Toast.LENGTH_SHORT).show()
            }
        }

        // Actualizamos el contador de entradas
        actualizarCistella()

        // Abrimos la cesta cuando pulsamos sobre su contador
        tvCistella.setOnClickListener {
            val intent = android.content.Intent(this, CistellaActivity::class.java)
            startActivity(intent)
        }

        // Configuramos la lista de eventos
        recycler.layoutManager = LinearLayoutManager(this)

        // Recuperamos los datos de los eventos enviados desde MainActivity
        val jsonStr = intent.getStringExtra("JSON_DATA")

        if (jsonStr != null) {
            // Convertimos los datos recibidos en una lista de eventos
            val jsonObj = JSONObject(jsonStr)
            val llistaEsdeveniments = JSONParser.parseEsdeveniments(jsonObj)

            // Configuramos el adaptador de eventos
            recycler.adapter = EsdevenimentsAdapter(llistaEsdeveniments) {
                actualizarCistella()
            }
        }
    }

    // Actualizamos el contador con el número actual de entradas
    private fun actualizarCistella() {
        tvCistella.text = "Cistella: ${Cistella.totalElements()}"
    }

    // Se ejecuta cuando volvemos a la cartelera
    override fun onResume() {
        super.onResume()

        // Actualizamos el contador al regresar de la cesta
        if (::tvCistella.isInitialized) {
            actualizarCistella()
        }
    }
}