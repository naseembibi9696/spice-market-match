package com.spicemarket.match.presentation.menu

import com.spicemarket.match.domain.model.OrderLine
import com.spicemarket.match.domain.model.Spice

enum class StallSlotState {
    COMPLETED,
    CURRENT,
    LOCKED
}

data class StallSlot(
    val number: Int,
    val spice: Spice,
    val state: StallSlotState
)

data class MenuUiState(
    val coins: Int = 0,
    val stall: Int = 1,
    val ordersFilled: Int = 0,
    val bestScore: Int = 0,
    val stallPoints: Int = 0,
    val stalls: List<StallSlot> = emptyList(),
    val orderLines: List<OrderLine> = emptyList(),
    val reducedMotion: Boolean = false
)
