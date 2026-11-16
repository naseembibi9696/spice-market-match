package com.spicemarket.match.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spicemarket.match.data.sample.SampleData
import com.spicemarket.match.domain.model.PlayerProgress
import com.spicemarket.match.domain.model.Spice
import com.spicemarket.match.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class RecipeEntry(
    val spice: Spice,
    val matched: Int,
    val unlocked: Boolean
)

data class RecipeBookUiState(
    val entries: List<RecipeEntry> = emptyList(),
    val coins: Int = 0
) {
    val empty: Boolean get() = entries.none { it.unlocked }
}

class RecipeBookViewModel(
    progressRepository: ProgressRepository
) : ViewModel() {

    val uiState: StateFlow<RecipeBookUiState> = progressRepository.progress
        .map { toUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = toUiState(progressRepository.progress.value)
        )

    private fun toUiState(progress: PlayerProgress): RecipeBookUiState = RecipeBookUiState(
        entries = SampleData.spices.map { spice ->
            val matched = progress.spiceCounts[spice] ?: 0
            RecipeEntry(spice = spice, matched = matched, unlocked = matched > 0)
        },
        coins = progress.coins
    )
}
