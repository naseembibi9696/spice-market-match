package com.spicemarket.match.domain.usecase

import com.spicemarket.match.domain.model.Jar

class ApplySwapUseCase {

    operator fun invoke(board: List<Jar>, first: Int, second: Int): List<Jar> {
        val next = board.toMutableList()
        val carried = next[first]
        next[first] = next[second]
        next[second] = carried
        return next
    }
}
