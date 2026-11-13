package com.kinema.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class EsdevenimentsAdapter(
    private val llista: List<Esdeveniment>,
    private val onCistellaChanged: () -> Unit
) : RecyclerView.Adapter<EsdevenimentsAdapter.ViewHolder>() {

    // Guardar la posición del evento seleccionado
    private var selectedPosition = RecyclerView.NO_POSITION

    // Relacionar los elementos del XML con el código
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNom: TextView = view.findViewById(R.id.tvNomEsdeveniment)
        val tvSub: TextView = view.findViewById(R.id.tvDataCategoria)
        val tvEntrades: TextView = view.findViewById(R.id.tvEntrades)
        val layout: LinearLayout = view as LinearLayout
    }

    // Crear la vista de cada evento
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_esdeveniment, parent, false)
        return ViewHolder(view)
    }

    // Mostrar la información de cada evento
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = llista[position]

        holder.tvNom.text = item.nom
        holder.tvSub.text = "${item.categoria} • ${item.dataHora}"

        // Eliminar las entradas anteriores para evitar duplicados
        holder.layout.removeViews(3, holder.layout.childCount - 3)

        val isSelected = position == selectedPosition

        // Si el evento está seleccionado, mostrar sus entradas generales
        if (isSelected) {

            holder.tvEntrades.visibility = View.VISIBLE
            holder.tvEntrades.text = "Tipus d'entrades:"

            // Recorrer los tipos de entrada del evento
            for (entrada in item.tipusEntrades) {
                // Mostrar solamente las entradas generales
                if (entrada.tipoZona.equals("general", ignoreCase = true)) {

                    // Crear un TextView para cada tipo de entrada
                    val tvEntrada = TextView(holder.itemView.context)

                    tvEntrada.text = "• ${entrada.nom}: ${entrada.preu}€"
                    tvEntrada.textSize = 14f
                    tvEntrada.setTextColor(
                        android.graphics.Color.parseColor("#1B5E20")
                    )
                    tvEntrada.setPadding(0, 8, 0, 8)

                    // Al pulsar una entrada, añadirla a la cesta
                    tvEntrada.setOnClickListener {
                        Cistella.afegir(item, entrada)

                        // Actualizar el contador de la cesta
                        onCistellaChanged()
                    }

                    // Añadir la entrada a la vista del evento
                    holder.layout.addView(tvEntrada)
                }
            }

        } else {
            // Si el evento no está seleccionado, ocultar sus entradas
            holder.tvEntrades.visibility = View.GONE
        }

        // Al pulsar el evento, mostrar u ocultar sus entradas
        holder.itemView.setOnClickListener {
            val previousIndex = selectedPosition

            selectedPosition =
                if (isSelected) RecyclerView.NO_POSITION
                else holder.bindingAdapterPosition

            // Actualizar el evento que estaba seleccionado
            if (previousIndex != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousIndex)
            }

            // Actualizar el evento actual
            notifyItemChanged(holder.bindingAdapterPosition)
        }
    }

    // Devolver el número total de eventos
    override fun getItemCount(): Int = llista.size
}
