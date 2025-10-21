package com.example.gestordeactivos

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var textBalance: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonAdd: LinearLayout
    private lateinit var buttonDistribution: LinearLayout
    private lateinit var adapter: AssetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textBalance = findViewById(R.id.text_balance)
        recyclerView = findViewById(R.id.recycler_assets)
        buttonAdd = findViewById(R.id.button_add)
        buttonDistribution = findViewById(R.id.button_distribution)

        recyclerView.layoutManager = LinearLayoutManager(this)

        updateBalance()
        updateAssetList()

        buttonAdd.setOnClickListener {
            val intent = Intent(this, AddInvestmentActivity::class.java)
            startActivity(intent)
        }

        buttonDistribution.setOnClickListener {
            val intent = Intent(this, DistributionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateBalance()
        updateAssetList()
    }

    private fun updateBalance() {
        val formattedBalance = String.format("%.2f€", BalanceStore.balance)
        textBalance.text = formattedBalance
    }

    private fun updateAssetList() {
        val assetList = InvestmentStore.investments.map { (symbol, amount) ->
            Asset(
                name = symbol,
                symbol = symbol,
                value = String.format("%.2f€", amount),
                percent = "",
                iconColor = "#00000000",
                showIcon = false
            )
        }

        adapter = AssetAdapter(assetList) {}
        recyclerView.adapter = adapter
    }
}
