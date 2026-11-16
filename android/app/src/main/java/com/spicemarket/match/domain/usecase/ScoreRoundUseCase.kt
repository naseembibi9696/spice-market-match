package com.spicemarket.match.domain.usecase

import com.spicemarket.match.domain.model.OrderLine
import com.spicemarket.match.domain.model.RoundResult

class ScoreRoundUseCase {

    operator fun invoke(
        stall: Int,
        win: Boolean,
        score: Int,
        coinsEarned: Int,
        lines: List<OrderLine>
    ): RoundResult {
        val filled = lines.count { it.filled }
        val bonus = if (win) COMPLETION_BONUS + PER_LINE_BONUS * filled else 0
        return RoundResult(
            stall = stall,
            win = win,
            score = score,
            coinsEarned = coinsEarned + bonus,
            stallPoints = if (win) filled else 0,
            lines = lines
        )
    }

    private companion object {
        const val COMPLETION_BONUS = 100
        const val PER_LINE_BONUS = 20
    }
}
