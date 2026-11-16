package com.spicemarket.match.data.repository

import com.spicemarket.match.core.config.GameConfig
import com.spicemarket.match.data.local.SpicePreferences
import com.spicemarket.match.domain.model.PlayerProgress
import com.spicemarket.match.domain.model.RoundResult
import com.spicemarket.match.domain.model.Spice
import com.spicemarket.match.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressRepositoryImpl(
    private val preferences: SpicePreferences
) : ProgressRepository {

    private val current = MutableStateFlow(read())

    override val progress: StateFlow<PlayerProgress> = current.asStateFlow()

    override fun applyRound(result: RoundResult, matched: Map<Spice, Int>) {
        val value = current.value
        val counts = value.spiceCounts.toMutableMap()
        matched.forEach { entry ->
            counts[entry.key] = (counts[entry.key] ?: 0) + entry.value
        }
        persist(
            value.copy(
                coins = value.coins + result.coinsEarned,
                stall = if (result.win) value.stall + 1 else value.stall,
                ordersFilled = if (result.win) value.ordersFilled + 1 else value.ordersFilled,
                bestScore = maxOf(value.bestScore, result.score),
                stallPoints = value.stallPoints + result.stallPoints,
                spiceCounts = counts
            )
        )
    }

    override fun spendCoins(amount: Int): Boolean {
        val value = current.value
        if (value.coins < amount) return false
        persist(value.copy(coins = value.coins - amount))
        return true
    }

    override fun reset() {
        persist(
            PlayerProgress(
                coins = GameConfig.START_COINS,
                stall = 1,
                ordersFilled = 0,
                bestScore = 0,
                stallPoints = 0,
                spiceCounts = Spice.entries.associateWith { 0 }
            )
        )
    }

    private fun persist(value: PlayerProgress) {
        preferences.writeInt(KEY_COINS, value.coins)
        preferences.writeInt(KEY_STALL, value.stall)
        preferences.writeInt(KEY_ORDERS, value.ordersFilled)
        preferences.writeInt(KEY_BEST, value.bestScore)
        preferences.writeInt(KEY_POINTS, value.stallPoints)
        value.spiceCounts.forEach { entry ->
            preferences.writeInt(KEY_SPICE + entry.key.name, entry.value)
        }
        current.value = value
    }

    private fun read(): PlayerProgress = PlayerProgress(
        coins = preferences.readInt(KEY_COINS, GameConfig.START_COINS),
        stall = preferences.readInt(KEY_STALL, 1).coerceAtLeast(1),
        ordersFilled = preferences.readInt(KEY_ORDERS, 0),
        bestScore = preferences.readInt(KEY_BEST, 0),
        stallPoints = preferences.readInt(KEY_POINTS, 0),
        spiceCounts = Spice.entries.associateWith { preferences.readInt(KEY_SPICE + it.name, 0) }
    )

    private companion object {
        const val KEY_COINS = "coins"
        const val KEY_STALL = "stall"
        const val KEY_ORDERS = "orders_filled"
        const val KEY_BEST = "best_score"
        const val KEY_POINTS = "stall_points"
        const val KEY_SPICE = "spice_count_"
    }
}
