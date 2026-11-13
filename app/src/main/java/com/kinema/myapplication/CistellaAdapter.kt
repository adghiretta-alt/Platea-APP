package com.kinema.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class CistellaAdapter(
    private val llista: MutableList<EntradaCistella>,
    private val actualitzarTotal: () -> Unit
) : RecyclerView.Adapter<CistellaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvNomEsdeveniment: TextView =
            view.findViewById(R.id.tvNomEsdeveniment)

        val tvNomEntrada: TextView =
            view.findViewById(R.id.tvNomEntrada)

        val tvQuantitatPreu: TextView =
            view.findViewById(R.id.tvQuantitatPreu)

        val btnEliminar: Button =
            view.findViewById(R.id.btnEliminarEntrada)
    }

    // Crear la vista de cada entrada de la cesta
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cistella, parent, false)

        return ViewHolder(view)
    }

    // Mostrar los datos de cada entrada
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = llista[position]

        // Mostrar el nombre del evento
        holder.tvNomEsdeveniment.text = item.esdevenimentNom

        // Mostrar el nombre del tipo de entrada
        holder.tvNomEntrada.text = item.entrada.nom

        // Calcular el precio total según la cantidad
        val total = item.entrada.preu * item.quantitat

        // Mostrar cantidad, precio por entrada y subtotal
        holder.tvQuantitatPreu.text =
            "${item.quantitat} x ${item.entrada.preu}€ = ${total}€"

        // Eliminar la entrada seleccionada de la cesta
        holder.btnEliminar.setOnClickListener {
            val posicion = holder.adapterPosition

            if (posicion != RecyclerView.NO_POSITION) {
                llista.removeAt(posicion)
                notifyItemRemoved(posicion)

                // Actualizar el precio total
                actualitzarTotal()
            }
        }
    }

    // Devolver el número de elementos de la cesta
    override fun getItemCount(): Int = llista.size
}