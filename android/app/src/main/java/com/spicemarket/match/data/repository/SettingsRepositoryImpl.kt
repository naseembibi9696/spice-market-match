package com.spicemarket.match.data.repository

import com.spicemarket.match.data.local.SpicePreferences
import com.spicemarket.match.domain.model.AppSettings
import com.spicemarket.match.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val preferences: SpicePreferences
) : SettingsRepository {

    private val current = MutableStateFlow(
        AppSettings(
            sound = preferences.readBoolean(KEY_SOUND, true),
            music = preferences.readBoolean(KEY_MUSIC, true),
            reducedMotion = preferences.readBoolean(KEY_MOTION, false)
        )
    )

    override val settings: StateFlow<AppSettings> = current.asStateFlow()

    override fun setSound(enabled: Boolean) {
        preferences.writeBoolean(KEY_SOUND, enabled)
        current.value = current.value.copy(sound = enabled)
    }

    override fun setMusic(enabled: Boolean) {
        preferences.writeBoolean(KEY_MUSIC, enabled)
        current.value = current.value.copy(music = enabled)
    }

    override fun setReducedMotion(enabled: Boolean) {
        preferences.writeBoolean(KEY_MOTION, enabled)
        current.value = current.value.copy(reducedMotion = enabled)
    }

    private companion object {
        const val KEY_SOUND = "sound_enabled"
        const val KEY_MUSIC = "music_enabled"
        const val KEY_MOTION = "reduced_motion"
    }
}
