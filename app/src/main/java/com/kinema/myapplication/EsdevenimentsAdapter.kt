package com.kinema.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EsdevenimentsAdapter(
    private val llista: List<Esdeveniment>
) : RecyclerView.Adapter<EsdevenimentsAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNom: TextView = view.findViewById(R.id.tvNomEsdeveniment)
        val tvSub: TextView = view.findViewById(R.id.tvDataCategoria)
        val tvEntrades: TextView = view.findViewById(R.id.tvEntrades)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_esdeveniment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = llista[position]
        holder.tvNom.text = item.nom
        holder.tvSub.text = "${item.categoria} • ${item.dataHora}"

        val isSelected = position == selectedPosition

        if (isSelected) {
            val sb = StringBuilder("Tipus d'entrades:\n")
            for (entrada in item.tipusEntrades) {
                if (entrada.nom.uppercase().contains("GENERAL")) {
                    sb.append("• ${entrada.nom}: ${entrada.preu}€\n")
                }

            }
            holder.tvEntrades.text = sb.toString().trim()
            holder.tvEntrades.visibility = View.VISIBLE
        } else {
            holder.tvEntrades.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val previousIndex = selectedPosition
            selectedPosition = if (isSelected) RecyclerView.NO_POSITION else holder.adapterPosition

            notifyItemChanged(previousIndex)
            notifyItemChanged(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = llista.size
}