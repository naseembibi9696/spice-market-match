package com.spicemarket.match.presentation.decor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spicemarket.match.domain.model.DecorItem
import com.spicemarket.match.domain.model.DecorKind
import com.spicemarket.match.domain.model.DecorState
import com.spicemarket.match.domain.model.PlayerProgress
import com.spicemarket.match.domain.repository.DecorRepository
import com.spicemarket.match.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DecorEntry(
    val id: String,
    val kind: DecorKind,
    val price: Int,
    val owned: Boolean,
    val placed: Boolean,
    val affordable: Boolean
)

data class StallDecorUiState(
    val coins: Int = 0,
    val entries: List<DecorEntry> = emptyList(),
    val placedAwning: String? = null,
    val placedLamp: String? = null
)

class StallDecorViewModel(
    private val decorRepository: DecorRepository,
    progressRepository: ProgressRepository
) : ViewModel() {

    private val catalog: List<DecorItem> = decorRepository.items

    val uiState: StateFlow<StallDecorUiState> = combine(
        decorRepository.state,
        progressRepository.progress
    ) { decor, progress ->
        toUiState(decor, progress)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = toUiState(decorRepository.state.value, progressRepository.progress.value)
    )

    fun buy(id: String) {
        val item = catalog.firstOrNull { it.id == id } ?: return
        if (decorRepository.buy(item)) decorRepository.place(item)
    }

    fun place(id: String) {
        val item = catalog.firstOrNull { it.id == id } ?: return
        decorRepository.place(item)
    }

    private fun toUiState(decor: DecorState, progress: PlayerProgress): StallDecorUiState =
        StallDecorUiState(
            coins = progress.coins,
            entries = catalog.map { item ->
                DecorEntry(
                    id = item.id,
                    kind = item.kind,
                    price = item.price,
                    owned = decor.owned.contains(item.id),
                    placed = decor.placed[item.kind] == item.id,
                    affordable = progress.coins >= item.price
                )
            },
            placedAwning = decor.placed[DecorKind.AWNING],
            placedLamp = decor.placed[DecorKind.LAMP]
        )
}
