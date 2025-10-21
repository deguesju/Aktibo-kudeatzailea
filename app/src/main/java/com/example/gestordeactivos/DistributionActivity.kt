package com.example.gestordeactivos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DistributionActivity : AppCompatActivity() {

    private lateinit var distributionsContainer: LinearLayout
    private lateinit var btnBack: Button
    private lateinit var btnCreateDistribution: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distribution)

        distributionsContainer = findViewById(R.id.distributions_container)
        btnBack = findViewById(R.id.btn_back)
        btnCreateDistribution = findViewById(R.id.btn_create_distribution)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnCreateDistribution.setOnClickListener {
            val intent = Intent(this, SelectAssetsActivity::class.java)
            startActivity(intent)
        }

        showDistributions()
    }

    private fun showDistributions() {
        distributionsContainer.removeAllViews()

        DistributionStore.distributions.forEachIndexed { index, distribution ->
            val assetView = layoutInflater.inflate(R.layout.item_asset_distribution, distributionsContainer, false)

            val name = assetView.findViewById<TextView>(R.id.text_name)
            val percent = assetView.findViewById<TextView>(R.id.text_percent)
            val checkbox = assetView.findViewById<CheckBox>(R.id.checkbox_active)

            name.text = distribution.name
            percent.text = distribution.assets.joinToString(", ") { "${it.name} ${it.assignedPercent}%" }

            checkbox.isChecked = distribution.isActive

            checkbox.setOnClickListener {
                if (!distribution.isActive) {
                    DistributionStore.distributions.forEach { it.isActive = false }
                    distribution.isActive = true
                } else {
                    distribution.isActive = false
                }
                showDistributions()
            }

            distributionsContainer.addView(assetView)
        }
    }
}
