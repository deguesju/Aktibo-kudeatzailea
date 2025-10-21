package com.example.gestordeactivos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectAssetsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AssetAdapter
    private lateinit var textSelectedCount: TextView
    private lateinit var inputSearch: EditText
    private lateinit var btnConfirm: Button

    private val assetList = mutableListOf<Asset>()
    private var selectedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_assets)

        recyclerView = findViewById(R.id.recycler_assets)
        textSelectedCount = findViewById(R.id.text_selected_count)
        btnConfirm = findViewById(R.id.btn_confirm)

        // Crear 20 activos de ejemplo
        for (i in 1..20) {
            assetList.add(
                Asset(
                    name = "Aktibo $i",
                    symbol = "SYM$i",
                    value = "${1000 + i * 10}€",
                    percent = "+${(i % 10) * 1.5}%",
                    iconColor = if (i % 3 == 0) "#FFD700" else if (i % 3 == 1) "#3C99FC" else "#FFA500",
                    showIcon = true
                )
            )
        }

        // Inicializar adapter con listener de selección
        adapter = AssetAdapter(assetList) { added ->
            selectedCount += if (added) 1 else -1
            textSelectedCount.text = selectedCount.toString()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnConfirm.setOnClickListener {
            val selectedAssets = assetList.filter { it.isSelected }
            if (selectedAssets.isEmpty()) return@setOnClickListener

            // Crear distribución sin activarla
            val distributionName = "Distribución ${DistributionStore.distributions.size + 1}"
            val newDistribution = Distribution(distributionName, selectedAssets, isActive = false)

            // Guardar en memoria
            DistributionStore.distributions.add(newDistribution)

            // Volver a DistributionActivity
            startActivity(Intent(this, DistributionActivity::class.java))
        }
    }
}
