package com.kinema.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class CartelleraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cartellera)

        val recycler = findViewById<RecyclerView>(R.id.recyclerEvents)
        recycler.layoutManager = LinearLayoutManager(this)

        // Recibir el JSON enviado desde MainActivity
        val jsonStr = intent.getStringExtra("JSON_DATA")
        if (jsonStr != null) {
            val jsonObj = JSONObject(jsonStr)
            val llistaEsdeveniments = JSONParser.parseEsdeveniments(jsonObj)

            // Conectar la lista procesada con el Adaptador
            recycler.adapter = EsdevenimentsAdapter(llistaEsdeveniments)
        }
    }
}