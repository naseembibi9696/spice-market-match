package com.spicemarket.match.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicemarket.match.R
import com.spicemarket.match.core.config.GameConfig
import com.spicemarket.match.core.di.AppViewModelFactory
import com.spicemarket.match.core.theme.SpiceAccent
import com.spicemarket.match.core.theme.SpiceBackground
import com.spicemarket.match.core.theme.SpicePrimary
import com.spicemarket.match.core.theme.SpicePrimaryDeep
import com.spicemarket.match.core.theme.SpiceSurfaceVariant
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.core.ui.AppBackground
import com.spicemarket.match.core.ui.SecondaryButton
import com.spicemarket.match.core.ui.SectionTitle
import com.spicemarket.match.presentation.dialog.ConfirmDialog

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory)
    val state by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val resetRequested by settingsViewModel.resetRequested.collectAsStateWithLifecycle()
    SettingsContent(
        state = state,
        resetRequested = resetRequested,
        onSound = { value -> settingsViewModel.setSound(value) },
        onMusic = { value -> settingsViewModel.setMusic(value) },
        onMotion = { value -> settingsViewModel.setReducedMotion(value) },
        onAskReset = { settingsViewModel.askReset() },
        onCancelReset = { settingsViewModel.cancelReset() },
        onConfirmReset = { settingsViewModel.confirmReset() },
        onBack = onBack
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    resetRequested: Boolean,
    onSound: (Boolean) -> Unit,
    onMusic: (Boolean) -> Unit,
    onMotion: (Boolean) -> Unit,
    onAskReset: () -> Unit,
    onCancelReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onBack: () -> Unit
) {
    AppBackground(
        overlay = Brush.verticalGradient(
            listOf(
                SpiceTextPrimary.copy(alpha = 0.92f),
                SpicePrimaryDeep.copy(alpha = 0.86f),
                SpiceTextPrimary.copy(alpha = 0.94f)
            )
        )
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle(
            title = stringResource(R.string.settings_title),
            subtitle = stringResource(R.string.settings_subtitle)
        )
        Spacer(modifier = Modifier.height(20.dp))
        SettingRow(
            label = stringResource(R.string.settings_sound),
            checked = state.sound,
            onChange = onSound
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingRow(
            label = stringResource(R.string.settings_music),
            checked = state.music,
            onChange = onMusic
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingRow(
            label = stringResource(R.string.settings_motion),
            checked = state.reducedMotion,
            onChange = onMotion
        )
        Spacer(modifier = Modifier.height(24.dp))
        SecondaryButton(
            text = stringResource(R.string.reset_progress),
            onClick = onAskReset,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(
                R.string.orders_footer,
                state.ordersFilled,
                GameConfig.TOTAL_ORDERS_GOAL
            ),
            style = MaterialTheme.typography.labelSmall,
            color = SpiceSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))
        SecondaryButton(
            text = stringResource(R.string.back_to_menu),
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(18.dp))
    }
    if (resetRequested) {
        ConfirmDialog(
            title = stringResource(R.string.reset_title),
            message = stringResource(R.string.reset_message),
            confirmText = stringResource(R.string.reset),
            cancelText = stringResource(R.string.cancel),
            onConfirm = onConfirmReset,
            onCancel = onCancelReset
        )
    }
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val stateLabel = if (checked) {
        stringResource(R.string.state_on)
    } else {
        stringResource(R.string.state_off)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(64.dp)
            .semantics { stateDescription = stateLabel },
        shape = RoundedCornerShape(16.dp),
        color = SpiceBackground,
        border = BorderStroke(1.dp, SpiceSurfaceVariant),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = SpiceTextPrimary,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SpiceBackground,
                    checkedTrackColor = SpiceAccent,
                    uncheckedThumbColor = SpiceSurfaceVariant,
                    uncheckedTrackColor = SpiceBackground,
                    uncheckedBorderColor = SpicePrimary.copy(alpha = 0.3f)
                )
            )
        }
    }
}
