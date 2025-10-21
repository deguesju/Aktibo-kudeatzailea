package com.example.gestordeactivos

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class AssetAdapter(
    private val assets: List<Asset>,
    private val onAddClick: (added: Boolean) -> Unit
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    inner class AssetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.text_name)
        val textSymbol: TextView = view.findViewById(R.id.text_symbol)
        val textValue: TextView = view.findViewById(R.id.text_value)
        val textPercent: TextView = view.findViewById(R.id.text_percent)
        val imageIcon: ImageView = view.findViewById(R.id.image_icon)
        val buttonAdd: ImageView = view.findViewById(R.id.button_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asset, parent, false)
        return AssetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assets[position]

        holder.textName.text = asset.name
        holder.textSymbol.text = asset.symbol
        holder.textValue.text = asset.value
        holder.textPercent.text = "${asset.assignedPercent}%"

        // 🔹 Mostrar u ocultar icono según showIcon
        if (asset.showIcon) {
            holder.imageIcon.visibility = View.VISIBLE
            asset.iconColor?.let { holder.imageIcon.setColorFilter(Color.parseColor(it)) }

            holder.buttonAdd.visibility = View.VISIBLE
            holder.buttonAdd.setImageResource(if (asset.isSelected) R.drawable.ic_check else R.drawable.ic_add)
        } else {
            holder.imageIcon.visibility = View.GONE
            holder.buttonAdd.visibility = View.GONE
        }

        // Cambiar fondo según selección
        holder.itemView.setBackgroundResource(
            if (asset.isSelected) R.drawable.bg_glass_card_gradient_selected
            else R.drawable.bg_glass_card_gradient
        )

        holder.buttonAdd.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val currentAsset = assets[currentPosition]

                if (!currentAsset.isSelected) {
                    val usedPercent = assets.sumOf { it.assignedPercent }
                    val availablePercent = 100 - usedPercent
                    if (availablePercent <= 0) return@setOnClickListener

                    val context = holder.itemView.context
                    val dialogView = LayoutInflater.from(context)
                        .inflate(R.layout.dialog_percentage, null)
                    val seekBar = dialogView.findViewById<SeekBar>(R.id.seekbar_percentage)
                    val textValue = dialogView.findViewById<TextView>(R.id.text_percent_value)

                    seekBar.max = availablePercent
                    seekBar.progress = availablePercent / 2
                    textValue.text = "${seekBar.progress}%"

                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            textValue.text = "$progress%"
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })

                    AlertDialog.Builder(context)
                        .setTitle("Portzentajea esleitu ${currentAsset.name}-ri")
                        .setView(dialogView)
                        .setPositiveButton("Onartu") { dialog, _ ->
                            currentAsset.assignedPercent = seekBar.progress
                            currentAsset.isSelected = true
                            notifyItemChanged(currentPosition)
                            onAddClick(true)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Ukatu") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    currentAsset.isSelected = false
                    currentAsset.assignedPercent = 0
                    notifyItemChanged(currentPosition)
                    onAddClick(false)
                }
            }
        }
    }

    override fun getItemCount(): Int = assets.size
}
