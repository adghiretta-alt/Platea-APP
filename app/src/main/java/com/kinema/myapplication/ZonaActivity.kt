package com.kinema.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class ZonaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_zona)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recycler = findViewById<RecyclerView>(R.id.recyclerSeients)
        val btnComprar = findViewById<Button>(R.id.btnComprarEntrades)
        val esdevName = findViewById<TextView>(R.id.tvCistella)
        val esdevId = intent.getStringExtra("esdevId")!!
        val esdevNom = intent.getStringExtra("esdevNom")!!
        esdevName.text = esdevNom
        @Suppress("DEPRECATION")
        val entrada = intent.getSerializableExtra("entrada") as TipusEntrada

        var mapaAdapter: MapaSeientsAdapter? = null

        WebSocketManager.respuestaMapa = { message ->

            val resposta = JSONObject(message)

            if (resposta.has("error")) {
                Toast.makeText(this, resposta.getString("error"), Toast.LENGTH_LONG).show()
                finish()
            } else {
                val files = resposta.getInt("files")
                val columnes = resposta.getInt("columnes")
                val matriu = resposta.getJSONArray("matriu")

                // 1) Aplanar la matriz 2D a una lista 1D de casillas
                val caselles = mutableListOf<SeientUI.Casella?>()

                for (f in 0 until files) {
                    val fila = matriu.getJSONArray(f)
                    for (c in 0 until columnes) {
                        val casella = fila.opt(c)
                        caselles.add(
                            if (casella is JSONArray)
                                SeientUI.Casella(
                                    casella.getString(0),   // zonaId, p.ej. "Z1"
                                    casella.getInt(1),      // fila
                                    casella.getInt(2),      // numero
                                    casella.getString(3)    // "L" o "V"
                                )
                            else null
                        )
                    }
                }

                // 2) Montar la rejilla (dentro de post: el width aún es 0 hasta el layout pass)
                recycler.post {
                    val cellSizePx = (recycler.width - 16) / columnes

                    mapaAdapter = MapaSeientsAdapter(caselles, entrada.zona, cellSizePx) {
                        // aquí actualizarás el total del botón en el pas 5
                    }

                    recycler.layoutManager = GridLayoutManager(this, columnes)
                    recycler.adapter = mapaAdapter
                    btnComprar.isEnabled = true
                }
            }
        }

        btnComprar.setOnClickListener {
            val adapter = mapaAdapter

            if (adapter == null || adapter.seleccionats.isEmpty()) {
                Toast.makeText(this, "Selecciona almenys un seient", Toast.LENGTH_LONG).show()
            } else {
                val seients = adapter.seleccionats.map {
                    val parts = it.split("-")
                    Pair(parts[1].toInt(), parts[2].toInt())
                }.toMutableList()

                Cistella.afegir(esdevId, esdevNom, entrada, seients)

                Toast.makeText(this, "Afegit a la cistella", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        val client = WebSocketManager.client

        if (client == null || !client.isOpen) {
            Toast.makeText(this, "No hay conexión con el servidor", Toast.LENGTH_LONG).show()
            finish()
        } else {
            val request = JSONObject().put("type", "mapa").put("esdev", esdevId)
            client.send(request.toString())
        }
    }

    override fun onDestroy() {
        WebSocketManager.respuestaMapa = null
        super.onDestroy()
    }
}