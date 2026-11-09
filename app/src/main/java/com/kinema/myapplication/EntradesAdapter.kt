package com.kinema.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EntradesAdapter(
    private val entrades: List<Entrada>
) : RecyclerView.Adapter<EntradesAdapter.EntradaViewHolder>() {

    // Representa cada elemento de la lista.
    class EntradaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDatosEntrada: TextView = view.findViewById(R.id.tvDatosEntrada)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entrada, parent, false)

        return EntradaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntradaViewHolder, position: Int) {

        val entrada = entrades[position]

        // Mostrar el número de entrada y sus datos.
        var texto = "Entrada ${position + 1}\n\n"
        texto += "Esdeveniment: ${entrada.esdeveniment}\n"
        texto += "Tipus d'entrada: ${entrada.tipusEntrada}\n"
        texto += "Zona: ${entrada.tipusZona}\n"

        // Mostrar el asiento si existe.
        if (entrada.fila != null && entrada.numero != null) {
            texto += "Seient: Fila ${entrada.fila}, número ${entrada.numero}\n"
        } else {
            texto += "Seient: No en té\n"
        }

        texto += "Titular: ${entrada.titular}"

        holder.tvDatosEntrada.text = texto
    }

    override fun getItemCount(): Int {
        return entrades.size
    }
}