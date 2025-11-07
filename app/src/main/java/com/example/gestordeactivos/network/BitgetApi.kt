package com.example.gestordeactivos.network

import retrofit2.http.GET
import retrofit2.http.Query

// Modelo para el ticker individual (data es un objeto)
data class BitgetTickerData(
    val symbol: String,
    val last: String,
    val changePercent: String? = null,
    val high24h: String? = null,
    val low24h: String? = null
)

// Respuesta cuando data es un objeto
data class BitgetResponseObject<T>(
    val code: String,
    val msg: String?,
    val requestTime: Long?,
    val data: T?            // data es un objeto (no una lista) para este endpoint
)

interface BitgetApi {
    // Endpoint para obtener el precio de un par (ej: BTCUSDT_SPBL)
    @GET("api/spot/v1/market/ticker")
    suspend fun getMarketData(
        @Query("symbol") symbol: String
    ): BitgetResponseObject<BitgetTickerData>
}
