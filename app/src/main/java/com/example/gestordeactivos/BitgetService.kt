package com.example.gestordeactivos

import com.example.gestordeactivos.network.BitgetApi
import com.example.gestordeactivos.network.RetrofitClient

object BitgetService {
    fun create(): BitgetApi {
        return RetrofitClient.instance.create(BitgetApi::class.java)
    }
}
