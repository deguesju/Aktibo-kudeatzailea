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
import kotlinx.coroutines.launch

class SelectAssetsActivity : AppCompatActivity() {

    // --- UI elementuak eta datuen kudeaketa ---
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AssetAdapter
    private lateinit var textSelectedCount: TextView
    private lateinit var inputSearch: EditText
    private lateinit var btnConfirm: Button

    private val assetList = mutableListOf<Asset>()   // API-tik jasotako aktiboak gordetzeko zerrenda
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



        // =======================================================
        // API-RA KONEXIOA ETA DATUEN ESKAERA
        // =======================================================

        lifecycleScope.launch {
            try {

                val api = BitgetService.create()
                val response = api.getAllTickers()


                // =======================================================
                // API ERANTZUNA BALIDATU
                // =======================================================


                if (response.code == "00000" && response.data != null) {

                    val desiredSymbols = setOf("BTCUSDT", "ETHUSDT", "SOLUSDT", "BNBUSDT", "XRPUSDT")


                    // =======================================================
                    // APIK EMANDAKO DATUAK FILTRATU (guk nahi ditugun aktiboak soilik)
                    // =======================================================

                    val filteredList = response.data.filter { it.symbol in desiredSymbols }

                    if (filteredList.isEmpty()) {
                        Toast.makeText(
                            this@SelectAssetsActivity,
                            "Ezin izan dira nahi ziren aktiboak topatu API erantzunean.",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                    // =======================================================
                    // API DATUAK UI-RA EGOKITU (Asset objektuak sortu)
                    // =======================================================

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
                                percent = tickerData.change24h?.let {
                                    if (it.startsWith("-")) it else "+$it"
                                } ?: "",
                                iconColor = "#3C99FC",
                                showIcon = true
                            )
                        )
                    }
                } else {
                    // =======================================================
                    // API-AK ERANTZUN OKER BAT EMATEN DUENEAN
                    // =======================================================
                    val errorMsg = "API errorea: ${response.msg ?: "Errore ezezaguna"}"
                    Log.e("BitgetAPI", "API deia huts egin du. Code: ${response.code}, msg: ${response.msg}")
                    Toast.makeText(this@SelectAssetsActivity, errorMsg, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                // =======================================================
                // SAREKO EDO PARSEATZEKO ERROREAK
                // =======================================================
                val errorMsg = "Sare/Parse Errorea: ${e.message ?: "Errore ezezaguna"}"
                Log.e("BitgetAPI", "Ustekabeko errorea gertatu da", e)
                Toast.makeText(this@SelectAssetsActivity, errorMsg, Toast.LENGTH_LONG).show()
            } finally {
                // --- RecyclerView eguneratu azken emaitzarekin ---
                adapter.notifyDataSetChanged()
            }
        }

        // =======================================================
        // AUKERATUTAKO AKTIBOAK KONFIRMATU ETA DISTRIBUZIOA SORTU
        // =======================================================
        btnConfirm.setOnClickListener {

            // --- Erabiltzaileak benetan zerbait aukeratu duen egiaztatu ---
            val selectedAssets = assetList.filter { it.isSelected }
            if (selectedAssets.isEmpty()) return@setOnClickListener

            // --- Distribuzio berri bat sortu ---
            val distributionName = "Distribución ${DistributionStore.distributions.size + 1}"
            val newDistribution = Distribution(distributionName, selectedAssets, isActive = false)

            // --- Memorian gorde ---
            DistributionStore.distributions.add(newDistribution)

            // --- Hurrengo pantailara joan ---
            startActivity(Intent(this, DistributionActivity::class.java))
        }
    }
}
