package com.spicemarket.match.domain.model

data class Jar(
    val id: Long,
    val spice: Spice,
    val special: Boolean = false
)

data class MatchRun(
    val indices: List<Int>,
    val spice: Spice
)

data class OrderLine(
    val spice: Spice,
    val target: Int,
    val cleared: Int = 0
) {
    val filled: Boolean get() = cleared >= target
    val remaining: Int get() = (target - cleared).coerceAtLeast(0)
}

data class StallOrder(
    val stall: Int,
    val lines: List<OrderLine>,
    val moves: Int
)

data class RoundResult(
    val stall: Int,
    val win: Boolean,
    val score: Int,
    val coinsEarned: Int,
    val stallPoints: Int,
    val lines: List<OrderLine>
)
