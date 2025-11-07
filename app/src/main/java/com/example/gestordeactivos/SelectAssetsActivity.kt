package com.example.gestordeactivos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordeactivos.network.BitgetTickerData
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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

        // Inicializar adapter vacío
        adapter = AssetAdapter(assetList) { added ->
            selectedCount += if (added) 1 else -1
            textSelectedCount.text = selectedCount.toString()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // --- Llamada a Bitget (corrutina) ---
        lifecycleScope.launch {
            try {
                val api = BitgetService.create()

                // símbolos válidos para spot en Bitget (usa sufijo _SPBL)
                val pairs = listOf(
                    "BTCUSDT_SPBL",
                    "ETHUSDT_SPBL",
                    "SOLUSDT_SPBL",
                    "BNBUSDT_SPBL",
                    "XRPUSDT_SPBL"
                )

                for (symbol in pairs) {
                    val response = api.getMarketData(symbol)

                    // log para depuración
                    Log.d("BitgetAPI", "Respuesta raw para $symbol -> code=${response.code} msg=${response.msg} data=${response.data}")

                    if (response.code == "00000" && response.data != null) {
                        val data: BitgetTickerData = response.data

                        assetList.add(
                            Asset(
                                name = when (symbol) {
                                    "BTCUSDT_SPBL" -> "Bitcoin"
                                    "ETHUSDT_SPBL" -> "Ethereum"
                                    "SOLUSDT_SPBL" -> "Solana"
                                    "BNBUSDT_SPBL" -> "BNB"
                                    "XRPUSDT_SPBL" -> "Ripple"
                                    else -> symbol
                                },
                                symbol = symbol,
                                value = "${data.last} USDT",
                                percent = data.changePercent?.let { if (it.startsWith("-")) it else "+$it" } ?: "",
                                iconColor = "#3C99FC",
                                showIcon = true
                            )
                        )
                    } else {
                        Log.e("BitgetAPI", "No data for $symbol -> code=${response.code} msg=${response.msg}")
                    }
                }

                // Si no se llenó la lista, añade fallback mock para que UI no quede vacía (opcional)
                if (assetList.isEmpty()) {
                    Log.w("BitgetAPI", "assetList vacío: añadiendo datos de ejemplo para probar UI")
                    assetList.addAll(
                        listOf(
                            Asset("Bitcoin", "BTCUSDT_SPBL", "—", "", "#FFD700", true),
                            Asset("Ethereum", "ETHUSDT_SPBL", "—", "", "#3C99FC", true)
                        )
                    )
                }

                // Actualizar UI en hilo principal
                adapter.notifyDataSetChanged()

            } catch (e: IOException) {
                Log.e("BitgetAPI", "Error de red: ${e.message}", e)
            } catch (e: HttpException) {
                Log.e("BitgetAPI", "Error HTTP: ${e.message}", e)
            } catch (e: Exception) {
                Log.e("BitgetAPI", "Excepción inesperada", e)
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
