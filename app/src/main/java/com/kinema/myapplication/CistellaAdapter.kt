
package com.kinema.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
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

        val tvQuantitat: TextView =
            view.findViewById(R.id.tvQuantitat)

        val btnMes: Button =
            view.findViewById(R.id.btnMes)

        val btnMenys: Button =
            view.findViewById(R.id.btnMenys)

        val btnEliminar: Button =
            view.findViewById(R.id.btnEliminarEntrada)
    }

    // Crear la vista de cada entrada de la cesta
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cistella, parent, false)

        return ViewHolder(view)
    }

    // Mostrar los datos de cada entrada
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = llista[position]

        // Mostrar el nombre del evento y del tipo de entrada
        holder.tvNomEsdeveniment.text = item.esdevenimentNom
        holder.tvNomEntrada.text = item.entrada.nom

        // Comprobar si la entrada es de tipo general
        val esGeneral = item.entrada.tipoZona.equals(
            "general",
            ignoreCase = true
        )

        if (esGeneral) {

            // Mostrar los controles de cantidad para entradas generales
            holder.btnMes.visibility = View.VISIBLE
            holder.btnMenys.visibility = View.VISIBLE
            holder.tvQuantitat.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE

            holder.tvQuantitat.text = item.quantitat.toString()

            // Actualizar el precio de la entrada
            actualizarPrecio(holder, item)

        } else {

            // Ocultar los controles de cantidad para entradas numeradas
            holder.btnMes.visibility = View.GONE
            holder.btnMenys.visibility = View.GONE
            holder.tvQuantitat.visibility = View.GONE

            // Mostrar el botón para eliminar la butaca
            holder.btnEliminar.visibility = View.VISIBLE

            // Mostrar únicamente el precio de la butaca
            holder.tvQuantitatPreu.text =
                "Preu: ${item.entrada.preu} €"
        }

        // Aumentar la cantidad de entradas
        holder.btnMes.setOnClickListener {

            item.quantitat++

            holder.tvQuantitat.text = item.quantitat.toString()

            actualizarPrecio(holder, item)
            actualitzarTotal()
        }

        // Disminuir la cantidad de entradas
        holder.btnMenys.setOnClickListener {

            if (item.quantitat > 1) {

                item.quantitat--

                holder.tvQuantitat.text = item.quantitat.toString()

                actualizarPrecio(holder, item)
                actualitzarTotal()

            } else {

                // Pedir confirmación si la cantidad llega a cero
                confirmarEliminacion(holder, item)
            }
        }

        // Eliminar una entrada de la cesta
        holder.btnEliminar.setOnClickListener {
            confirmarEliminacion(holder, item)
        }
    }

    // Calcular y mostrar el precio de las entradas generales
    private fun actualizarPrecio(
        holder: ViewHolder,
        item: EntradaCistella
    ) {

        val total = item.entrada.preu * item.quantitat

        holder.tvQuantitatPreu.text =
            "${item.quantitat} x ${item.entrada.preu} € = ${total} €"
    }

    // Mostrar un diálogo de confirmación antes de eliminar
    private fun confirmarEliminacion(
        holder: ViewHolder,
        item: EntradaCistella
    ) {

        // Comprobar si se trata de una entrada general
        val esGeneral = item.entrada.tipoZona.equals(
            "general",
            ignoreCase = true
        )

        // Preparar el título y el mensaje del diálogo
        val titulo = if (esGeneral) {
            "Eliminar entrada"
        } else {
            "Eliminar butaca"
        }

        val mensaje = if (esGeneral) {
            "Vols eliminar aquesta entrada de la cistella?"
        } else {
            "Vols eliminar aquesta butaca de la cistella?"
        }

        AlertDialog.Builder(holder.itemView.context)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Sí, eliminar") { _, _ ->

                // Buscar la posición actual de la entrada
                val posicion = llista.indexOf(item)

                if (posicion != -1) {

                    // Eliminar la entrada de la lista
                    llista.removeAt(posicion)

                    // Notificar el cambio al RecyclerView
                    notifyItemRemoved(posicion)
                    notifyItemRangeChanged(
                        posicion,
                        llista.size - posicion
                    )

                    // Actualizar el precio total
                    actualitzarTotal()
                }
            }
            .setNegativeButton("Cancel·lar", null)
            .show()
    }

    // Devolver el número de elementos de la cesta
    override fun getItemCount(): Int = llista.size
}