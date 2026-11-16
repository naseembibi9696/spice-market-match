package com.spicemarket.match.domain.usecase

import com.spicemarket.match.domain.model.Jar

class FindHintUseCase(
    private val findMatches: FindMatchesUseCase,
    private val applySwap: ApplySwapUseCase
) {

    operator fun invoke(board: List<Jar>, cols: Int, rows: Int): Pair<Int, Int>? {
        for (index in board.indices) {
            val row = index / cols
            val col = index % cols
            if (col + 1 < cols) {
                val other = index + 1
                if (findMatches(applySwap(board, index, other), cols, rows).isNotEmpty()) {
                    return index to other
                }
            }
            if (row + 1 < rows) {
                val other = index + cols
                if (findMatches(applySwap(board, index, other), cols, rows).isNotEmpty()) {
                    return index to other
                }
            }
        }
        return null
    }

    fun hasLegalMove(board: List<Jar>, cols: Int, rows: Int): Boolean =
        invoke(board, cols, rows) != null
}
