package com.spicemarket.match.domain.usecase

import com.spicemarket.match.domain.model.Jar
import com.spicemarket.match.domain.model.Spice
import kotlin.random.Random

class ResolveCascadeUseCase {

    fun collapse(
        board: List<Jar>,
        cleared: Set<Int>,
        cols: Int,
        rows: Int,
        random: Random,
        nextId: () -> Long
    ): List<Jar> {
        val palette = Spice.entries
        val grid = board.toMutableList()
        for (col in 0 until cols) {
            val survivors = ArrayList<Jar>(rows)
            for (row in rows - 1 downTo 0) {
                val index = row * cols + col
                if (cleared.contains(index).not()) survivors.add(grid[index])
            }
            var write = rows - 1
            for (jar in survivors) {
                grid[write * cols + col] = jar
                write--
            }
            while (write >= 0) {
                grid[write * cols + col] = Jar(nextId(), palette[random.nextInt(palette.size)])
                write--
            }
        }
        return grid
    }
}
