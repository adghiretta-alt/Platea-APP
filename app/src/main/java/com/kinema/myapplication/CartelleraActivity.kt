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
import android.widget.ImageButton

class CartelleraActivity : AppCompatActivity() {

    companion object {
        var estaAbierta = false
    }

    // Variable que muestra el número de entradas de la cesta
    private lateinit var btnCistella: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cartellera)
        // Recuperamos el icono de configuración.
        val btnConfiguracio = findViewById<ImageButton>(R.id.btnConfiguracio)

        // Abrimos la pantalla de configuración al pulsarlo.
        btnConfiguracio.setOnClickListener {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }

        // Obtenemos los elementos de la pantalla
        val recycler = findViewById<RecyclerView>(R.id.recyclerSeients)
        btnCistella = findViewById(R.id.btnCistella)

        // Botón para consultar las entradas compradas.
        val btnMisEntrades = findViewById<Button>(R.id.btnComprarEntrades)

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
        btnCistella.setOnClickListener {
            val intent = android.content.Intent(this, CistellaActivity::class.java)
            startActivity(intent)
        }

        // Configuramos la lista de eventos
        recycler.layoutManager = LinearLayoutManager(this)

        // Registra la recepción de nuevas respuestas de la cartelera.
        WebSocketManager.respuestaCartelera = { respuesta ->
            val jsonObj = JSONObject(respuesta)
            val llistaEsdeveniments = JSONParser.parseEsdeveniments(jsonObj)

            recycler.adapter = EsdevenimentsAdapter(llistaEsdeveniments) {
                actualizarCistella()
            }
        }

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
        btnCistella.text = Cistella.totalElements().toString()
    }

    // Se ejecuta cuando volvemos a la cartelera
    override fun onResume() {
        super.onResume()

        // Actualizamos el contador al regresar de la cesta
        if (::btnCistella.isInitialized) {
            actualizarCistella()
        }
    }
    // Indica que la cartelera está abierta.
    override fun onStart() {
        super.onStart()
        estaAbierta = true
    }

    // Indica que la cartelera está cerrada.
    override fun onStop() {
        super.onStop()
        estaAbierta = false
    }

    // Libera la referencia al cerrar la cartelera.
    override fun onDestroy() {
        super.onDestroy()
        WebSocketManager.respuestaCartelera = null
    }
}