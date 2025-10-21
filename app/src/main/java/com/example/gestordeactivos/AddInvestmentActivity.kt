package com.example.gestordeactivos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddInvestmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ingreso_main)

        val inputAmount = findViewById<EditText>(R.id.input_amount)
        val btnConfirm = findViewById<Button>(R.id.btn_confirm)
        val btnBack = findViewById<Button>(R.id.btn_back)

        btnConfirm.setOnClickListener {
            val amountText = inputAmount.text.toString()

            if (amountText.isBlank()) {
                Toast.makeText(this, "Introduce una cantidad válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Cantidad no válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sumar al balance global
            BalanceStore.balance += amount

            // Buscar la distribución activa
            val activeDistribution = DistributionStore.distributions.find { it.isActive }

            if (activeDistribution == null) {
                Toast.makeText(this, "No hay ninguna distribución activa", Toast.LENGTH_SHORT).show()
            } else {
                distributeInvestment(amount, activeDistribution)
                Toast.makeText(this, "Repartido $amount€ según la distribución activa", Toast.LENGTH_SHORT).show()
            }

            // Volver al main
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun distributeInvestment(amount: Double, distribution: Distribution) {
        for (asset in distribution.assets) {
            val percent = asset.assignedPercent.toDouble() / 100.0
            val allocated = amount * percent

            InvestmentStore.investments[asset.symbol] =
                (InvestmentStore.investments[asset.symbol] ?: 0.0) + allocated
        }
    }
}
