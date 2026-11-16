package com.spicemarket.match.domain.usecase

import com.spicemarket.match.domain.model.Jar
import com.spicemarket.match.domain.model.Spice
import kotlin.random.Random

class GenerateBoardUseCase(
    private val findHint: FindHintUseCase
) {

    operator fun invoke(cols: Int, rows: Int, random: Random, nextId: () -> Long): List<Jar> {
        val palette = Spice.entries
        var last = emptyList<Jar>()
        repeat(ATTEMPTS) {
            val cells = ArrayList<Jar>(cols * rows)
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val banned = HashSet<Spice>()
                    if (col >= 2) {
                        val left = cells[row * cols + col - 1].spice
                        if (left == cells[row * cols + col - 2].spice) banned.add(left)
                    }
                    if (row >= 2) {
                        val above = cells[(row - 1) * cols + col].spice
                        if (above == cells[(row - 2) * cols + col].spice) banned.add(above)
                    }
                    val options = palette.filter { banned.contains(it).not() }
                    cells.add(Jar(nextId(), options[random.nextInt(options.size)]))
                }
            }
            last = cells
            if (findHint.hasLegalMove(cells, cols, rows)) return cells
        }
        return last
    }

    private companion object {
        const val ATTEMPTS = 24
    }
}
