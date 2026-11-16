package com.spicemarket.match.domain.usecase

import com.spicemarket.match.domain.model.Jar
import com.spicemarket.match.domain.model.MatchRun

class FindMatchesUseCase {

    operator fun invoke(board: List<Jar>, cols: Int, rows: Int): List<MatchRun> {
        val runs = mutableListOf<MatchRun>()
        for (row in 0 until rows) {
            var start = 0
            while (start < cols) {
                val spice = board[row * cols + start].spice
                var end = start + 1
                while (end < cols && board[row * cols + end].spice == spice) end++
                if (end - start >= 3) {
                    runs.add(MatchRun((start until end).map { row * cols + it }, spice))
                }
                start = end
            }
        }
        for (col in 0 until cols) {
            var start = 0
            while (start < rows) {
                val spice = board[start * cols + col].spice
                var end = start + 1
                while (end < rows && board[end * cols + col].spice == spice) end++
                if (end - start >= 3) {
                    runs.add(MatchRun((start until end).map { it * cols + col }, spice))
                }
                start = end
            }
        }
        return runs
    }
}
