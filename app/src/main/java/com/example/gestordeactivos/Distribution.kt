package com.example.gestordeactivos

data class Distribution(
    val name: String,
    val assets: List<Asset>,
    var isActive: Boolean = false // nuevo campo para activar/desactivar
)
