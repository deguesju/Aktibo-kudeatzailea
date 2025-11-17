package com.example.gestordeactivos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordeactivos.network.BitgetApi
import kotlinx.coroutines.launch

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

        adapter = AssetAdapter(assetList) { added ->
            selectedCount += if (added) 1 else -1
            textSelectedCount.text = selectedCount.toString()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            try {
                val api = BitgetService.create()
                val response = api.getAllTickers()

                if (response.code == "00000" && response.data != null) {
                    val desiredSymbols = setOf("BTCUSDT", "ETHUSDT", "SOLUSDT", "BNBUSDT", "XRPUSDT")

                    val filteredList = response.data.filter { it.symbol in desiredSymbols }

                    if (filteredList.isEmpty()) {
                        Toast.makeText(this@SelectAssetsActivity, "Could not find desired assets in API response.", Toast.LENGTH_LONG).show()
                    }

                    for (tickerData in filteredList) {
                        assetList.add(
                            Asset(
                                name = when (tickerData.symbol) {
                                    "BTCUSDT" -> "Bitcoin"
                                    "ETHUSDT" -> "Ethereum"
                                    "SOLUSDT" -> "Solana"
                                    "BNBUSDT" -> "BNB"
                                    "XRPUSDT" -> "Ripple"
                                    else -> tickerData.symbol
                                },
                                symbol = tickerData.symbol,
                                value = tickerData.lastPr?.let { "$it USDT" } ?: "—",
                                percent = tickerData.change24h?.let { if (it.startsWith("-")) it else "+$it" } ?: "",
                                iconColor = "#3C99FC",
                                showIcon = true
                            )
                        )
                    }
                } else {
                    val errorMsg = "API Error: ${response.msg ?: "Unknown error"}"
                    Log.e("BitgetAPI", "API call failed with code: ${response.code} and message: ${response.msg}")
                    Toast.makeText(this@SelectAssetsActivity, errorMsg, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                val errorMsg = "Network/Parsing Error: ${e.message ?: "Unknown error"}"
                Log.e("BitgetAPI", "An unexpected error occurred", e)
                Toast.makeText(this@SelectAssetsActivity, errorMsg, Toast.LENGTH_LONG).show()
            } finally {
                adapter.notifyDataSetChanged()
            }
        }

        btnConfirm.setOnClickListener {
            val selectedAssets = assetList.filter { it.isSelected }
            if (selectedAssets.isEmpty()) return@setOnClickListener

            val distributionName = "Distribución ${DistributionStore.distributions.size + 1}"
            val newDistribution = Distribution(distributionName, selectedAssets, isActive = false)
            DistributionStore.distributions.add(newDistribution)

            startActivity(Intent(this, DistributionActivity::class.java))
        }
    }
}
