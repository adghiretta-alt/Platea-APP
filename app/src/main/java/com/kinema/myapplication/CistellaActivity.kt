package com.kinema.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CistellaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar el diseño de la pantalla de la cesta
        setContentView(R.layout.activity_cistella)

        // Buscar la lista de entradas en el diseño
        val recycler = findViewById<RecyclerView>(R.id.recyclerCistella)

        // Buscar el texto donde mostraremos el total
        val tvTotal = findViewById<TextView>(R.id.tvTotalCistella)

        // Organizar las entradas en una lista vertical
        recycler.layoutManager = LinearLayoutManager(this)

        // Conectar los datos de la cesta con el adaptador
        recycler.adapter = CistellaAdapter(Cistella.entrades)

        // Calcular y mostrar el precio total de la cesta
        tvTotal.text = "Total: ${Cistella.calcularTotal()}€"
    }
}