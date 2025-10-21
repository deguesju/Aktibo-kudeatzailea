package com.example.gestordeactivos

object InvestmentStore {
    val investments = mutableMapOf<String, Double>()  // symbol -> cantidad invertida

    fun clear() {
        investments.clear()
    }
}
