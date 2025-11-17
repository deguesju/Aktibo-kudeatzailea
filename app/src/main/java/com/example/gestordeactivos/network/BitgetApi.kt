package com.example.gestordeactivos.network

import retrofit2.http.GET

// Modelo para el ticker individual (API v2)
data class BitgetTickerData(
    val symbol: String,
    val lastPr: String?, // Campo actualizado: last -> lastPr
    val change24h: String? // Campo actualizado: changePercent -> change24h
)

// La respuesta de la API v2 contiene una lista en el campo 'data'
data class BitgetResponseList<T>(
    val code: String,
    val msg: String?,
    val requestTime: Long?,
    val data: List<T>?
)

interface BitgetApi {
    // Endpoint actualizado a v2 para obtener todos los tickers en una sola llamada
    @GET("api/v2/spot/market/tickers")
    suspend fun getAllTickers(): BitgetResponseList<BitgetTickerData>
}
