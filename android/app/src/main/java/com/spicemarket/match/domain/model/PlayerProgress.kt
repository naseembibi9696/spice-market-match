package com.spicemarket.match.domain.model

data class PlayerProgress(
    val coins: Int,
    val stall: Int,
    val ordersFilled: Int,
    val bestScore: Int,
    val stallPoints: Int,
    val spiceCounts: Map<Spice, Int>
)
