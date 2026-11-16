package com.spicemarket.match.domain.repository

import com.spicemarket.match.domain.model.PlayerProgress
import com.spicemarket.match.domain.model.RoundResult
import com.spicemarket.match.domain.model.Spice
import kotlinx.coroutines.flow.StateFlow

interface ProgressRepository {
    val progress: StateFlow<PlayerProgress>
    fun applyRound(result: RoundResult, matched: Map<Spice, Int>)
    fun spendCoins(amount: Int): Boolean
    fun reset()
}
