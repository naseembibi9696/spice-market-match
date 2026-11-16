package com.spicemarket.match.data.repository

import com.spicemarket.match.data.local.SpicePreferences
import com.spicemarket.match.data.sample.SampleData
import com.spicemarket.match.domain.model.DecorItem
import com.spicemarket.match.domain.model.DecorKind
import com.spicemarket.match.domain.model.DecorState
import com.spicemarket.match.domain.repository.DecorRepository
import com.spicemarket.match.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DecorRepositoryImpl(
    private val preferences: SpicePreferences,
    private val progressRepository: ProgressRepository
) : DecorRepository {

    override val items: List<DecorItem> = SampleData.decorItems

    private val current = MutableStateFlow(read())

    override val state: StateFlow<DecorState> = current.asStateFlow()

    override fun buy(item: DecorItem): Boolean {
        if (current.value.owned.contains(item.id)) return false
        if (progressRepository.spendCoins(item.price).not()) return false
        persist(current.value.copy(owned = current.value.owned + item.id))
        return true
    }

    override fun place(item: DecorItem) {
        if (current.value.owned.contains(item.id).not()) return
        persist(current.value.copy(placed = current.value.placed + (item.kind to item.id)))
    }

    override fun reset() {
        persist(DecorState())
    }

    private fun persist(value: DecorState) {
        preferences.writeText(KEY_OWNED, value.owned.joinToString(separator = ","))
        preferences.writeText(
            KEY_PLACED,
            value.placed.entries.joinToString(separator = ",") { it.key.name + ":" + it.value }
        )
        current.value = value
    }

    private fun read(): DecorState {
        val owned = preferences.readText(KEY_OWNED, "")
            .split(",")
            .filter { it.isNotBlank() }
            .toSet()
        val placed = preferences.readText(KEY_PLACED, "")
            .split(",")
            .filter { it.contains(":") }
            .mapNotNull { entry ->
                val parts = entry.split(":")
                val kind = DecorKind.entries.firstOrNull { it.name == parts[0] }
                if (kind == null) null else kind to parts[1]
            }
            .toMap()
        return DecorState(owned, placed)
    }

    private companion object {
        const val KEY_OWNED = "decor_owned"
        const val KEY_PLACED = "decor_placed"
    }
}
