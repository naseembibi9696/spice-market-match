package com.spicemarket.match.domain.usecase

import com.spicemarket.match.core.config.GameConfig
import com.spicemarket.match.domain.model.OrderLine
import com.spicemarket.match.domain.model.Spice
import com.spicemarket.match.domain.model.StallOrder
import kotlin.random.Random

class BuildStallOrderUseCase {

    operator fun invoke(stall: Int): StallOrder {
        val safeStall = stall.coerceAtLeast(1)
        val random = Random(safeStall * 7919L)
        val lineCount = if (safeStall >= 7) 3 else 2
        val spices = Spice.entries.shuffled(random).take(lineCount)
        val base = 6 + ((safeStall - 1) * 2) % 9
        val lines = spices.mapIndexed { index, spice ->
            OrderLine(spice, (base - index * 4).coerceIn(6, 16))
        }
        val moves = (GameConfig.MOVES_BASE - minOf(8, safeStall - 1))
            .coerceAtLeast(GameConfig.MIN_MOVES)
        return StallOrder(safeStall, lines, moves)
    }
}
