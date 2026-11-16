package com.spicemarket.match.domain.repository

import com.spicemarket.match.domain.model.AppSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    fun setSound(enabled: Boolean)
    fun setMusic(enabled: Boolean)
    fun setReducedMotion(enabled: Boolean)
}
