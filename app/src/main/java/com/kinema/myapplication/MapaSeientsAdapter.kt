package com.kinema.myapplication

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MapaSeientsAdapter(
    private val caselles: List<SeientUI.Casella?>,
    private val zonaActiva: String,
    private val cellSizePx: Int,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<MapaSeientsAdapter.SeientViewHolder>() {

    val seleccionats = mutableSetOf<String>()

    class SeientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView = (view as FrameLayout).getChildAt(0) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeientViewHolder {
        val root = FrameLayout(parent.context)
        val content = TextView(parent.context)
        content.gravity = Gravity.CENTER
        content.textSize = 8f
        val gap = maxOf(2, cellSizePx / 6)
        val params = FrameLayout.LayoutParams(
            cellSizePx - gap,
            cellSizePx - gap,
            Gravity.CENTER
        )
        root.layoutParams = RecyclerView.LayoutParams(cellSizePx, cellSizePx)
        root.addView(content, params)
        return SeientViewHolder(root)
    }

    override fun onBindViewHolder(holder: SeientViewHolder, position: Int) {
        val casella = caselles[position]
        val content = holder.content
        content.setOnClickListener(null)
        content.isClickable = false

        if (casella == null) {
            content.text = ""
            content.background = null
            return
        }

        if (casella.zonaId != zonaActiva) {
            content.text = casella.zonaId
            content.setTextColor(Color.parseColor("#9E9E9E"))
            content.background = null
            return
        }

        content.text = ""
        content.setTextColor(Color.TRANSPARENT)
        val color = when {
            casella.estat == "V" -> "#E53935"
            casella.key in seleccionats -> "#1E88E5"
            else -> "#4CAF50"
        }
        content.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(color))
        }

        if (casella.estat == "L") {
            content.isClickable = true
            content.setOnClickListener {
                if (casella.key in seleccionats) {
                    seleccionats.remove(casella.key)
                } else {
                    seleccionats.add(casella.key)
                }
                notifyItemChanged(holder.bindingAdapterPosition)
                onSelectionChanged()
            }
        }
    }

    override fun getItemCount(): Int = caselles.size
}
