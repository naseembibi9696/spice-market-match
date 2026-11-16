package com.spicemarket.match.domain.repository

import com.spicemarket.match.domain.model.DecorItem
import com.spicemarket.match.domain.model.DecorState
import kotlinx.coroutines.flow.StateFlow

interface DecorRepository {
    val items: List<DecorItem>
    val state: StateFlow<DecorState>
    fun buy(item: DecorItem): Boolean
    fun place(item: DecorItem)
    fun reset()
}
