package com.spicemarket.match.domain.model

enum class DecorKind {
    AWNING,
    LAMP,
    TRIM
}

data class DecorItem(
    val id: String,
    val kind: DecorKind,
    val price: Int
)

data class DecorState(
    val owned: Set<String> = emptySet(),
    val placed: Map<DecorKind, String> = emptyMap()
)
