package com.spicemarket.match.core.di

import android.content.Context
import com.spicemarket.match.data.local.SpicePreferences
import com.spicemarket.match.data.repository.DecorRepositoryImpl
import com.spicemarket.match.data.repository.ProgressRepositoryImpl
import com.spicemarket.match.data.repository.SettingsRepositoryImpl
import com.spicemarket.match.domain.repository.DecorRepository
import com.spicemarket.match.domain.repository.ProgressRepository
import com.spicemarket.match.domain.repository.SettingsRepository
import com.spicemarket.match.domain.usecase.ApplySwapUseCase
import com.spicemarket.match.domain.usecase.BuildStallOrderUseCase
import com.spicemarket.match.domain.usecase.FindHintUseCase
import com.spicemarket.match.domain.usecase.FindMatchesUseCase
import com.spicemarket.match.domain.usecase.GenerateBoardUseCase
import com.spicemarket.match.domain.usecase.ResolveCascadeUseCase
import com.spicemarket.match.domain.usecase.ScoreRoundUseCase

object ServiceLocator {

    private var preferences: SpicePreferences? = null

    fun init(context: Context) {
        if (preferences == null) {
            preferences = SpicePreferences(context.applicationContext)
        }
    }

    private val store: SpicePreferences
        get() = requireNotNull(preferences)

    val progressRepository: ProgressRepository by lazy { ProgressRepositoryImpl(store) }

    val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl(store) }

    val decorRepository: DecorRepository by lazy {
        DecorRepositoryImpl(store, progressRepository)
    }

    val findMatches = FindMatchesUseCase()
    val applySwap = ApplySwapUseCase()
    val findHint = FindHintUseCase(findMatches, applySwap)
    val generateBoard = GenerateBoardUseCase(findHint)
    val resolveCascade = ResolveCascadeUseCase()
    val buildStallOrder = BuildStallOrderUseCase()
    val scoreRound = ScoreRoundUseCase()
}
