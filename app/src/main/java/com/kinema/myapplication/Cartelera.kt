package com.kinema.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartelleraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cartellera)

        val recycler = findViewById<RecyclerView>(R.id.recyclerEvents)

        recycler.layoutManager = LinearLayoutManager(this)
    }
}