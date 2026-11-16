package com.spicemarket.match.presentation.menu

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicemarket.match.R
import com.spicemarket.match.core.config.GameConfig
import com.spicemarket.match.core.di.AppViewModelFactory
import com.spicemarket.match.core.theme.SpiceAccent
import com.spicemarket.match.core.theme.SpiceBackground
import com.spicemarket.match.core.theme.SpiceOnDark
import com.spicemarket.match.core.theme.SpicePrimary
import com.spicemarket.match.core.theme.SpiceSage
import com.spicemarket.match.core.theme.SpiceSurfaceVariant
import com.spicemarket.match.core.theme.SpiceTerracotta
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.core.ui.CoinBadge
import com.spicemarket.match.core.ui.PrimaryButton
import com.spicemarket.match.core.ui.SecondaryButton
import com.spicemarket.match.core.ui.SpiceJar
import com.spicemarket.match.core.ui.StatCard
import com.spicemarket.match.core.ui.spiceNameRes
import com.spicemarket.match.core.util.formatNumber

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onDecor: () -> Unit,
    onRecipes: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory)
    val state by menuViewModel.uiState.collectAsStateWithLifecycle()
    MenuContent(
        state = state,
        onPlay = onPlay,
        onDecor = onDecor,
        onRecipes = onRecipes,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onDecor: () -> Unit,
    onRecipes: () -> Unit,
    onSettings: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            SpiceTextPrimary.copy(alpha = 0.35f),
                            Color.Transparent,
                            SpiceBackground.copy(alpha = 0.85f),
                            SpiceBackground
                        )
                    )
                )
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = stringResource(R.string.cd_logo),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.menu_word_mark),
                        style = MaterialTheme.typography.labelLarge,
                        color = SpiceOnDark
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    CoinBadge(coins = state.coins)
                }
                Spacer(modifier = Modifier.weight(1f))
                StallPath(state = state)
                Spacer(modifier = Modifier.weight(1f))
            }
            MenuSheet(
                state = state,
                onPlay = onPlay,
                onDecor = onDecor,
                onRecipes = onRecipes,
                onSettings = onSettings
            )
        }
    }
}

@Composable
private fun StallPath(state: MenuUiState) {
    val transition = rememberInfiniteTransition(label = "stall")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (state.reducedMotion) 1f else 1.06f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "pulse"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        state.stalls.forEach { slot ->
            StallPlaque(slot = slot, pulse = pulse)
        }
    }
}

@Composable
private fun StallPlaque(slot: StallSlot, pulse: Float) {
    val current = slot.state == StallSlotState.CURRENT
    val locked = slot.state == StallSlotState.LOCKED
    val stateLabel = when (slot.state) {
        StallSlotState.COMPLETED -> stringResource(R.string.state_completed)
        StallSlotState.CURRENT -> stringResource(R.string.state_unlocked)
        StallSlotState.LOCKED -> stringResource(R.string.state_locked)
    }
    val spiceName = stringResource(spiceNameRes(slot.spice))
    val label = stringResource(R.string.cd_stall, slot.number, spiceName)
    Box(
        modifier = Modifier
            .padding(top = if (current) 0.dp else 10.dp)
            .graphicsLayer {
                scaleX = if (current) pulse else 1f
                scaleY = if (current) pulse else 1f
                alpha = if (locked) 0.45f else 1f
            }
            .semantics { stateDescription = stateLabel }
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(14.dp),
            color = SpiceSurfaceVariant,
            shadowElevation = 6.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                SpiceJar(
                    spice = slot.spice,
                    size = 44.dp,
                    label = label,
                    stateLabel = stateLabel,
                    showSlot = false
                )
            }
        }
        if (current) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .border(2.dp, SpiceAccent, RoundedCornerShape(14.dp))
            )
        }
        if (slot.state == StallSlotState.COMPLETED) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .background(SpiceSage, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = SpiceOnDark
                )
            }
        }
    }
}

@Composable
private fun MenuSheet(
    state: MenuUiState,
    onPlay: () -> Unit,
    onDecor: () -> Unit,
    onRecipes: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = SpiceBackground,
        shadowElevation = 20.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(4.dp)
                    .background(SpiceSurfaceVariant, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.menu_title),
                style = MaterialTheme.typography.headlineMedium,
                color = SpiceTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.menu_tagline, state.stall),
                style = MaterialTheme.typography.bodyMedium,
                color = SpiceTextPrimary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            MenuStats(state = state)
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                text = stringResource(R.string.play),
                onClick = onPlay,
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.decor),
                    onClick = onDecor,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.recipes),
                    onClick = onRecipes,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.settings),
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MenuStats(state: MenuUiState) {
    val ordersLabel = stringResource(R.string.stat_orders)
    val ordersValue = stringResource(
        R.string.orders_progress,
        state.ordersFilled,
        GameConfig.TOTAL_ORDERS_GOAL
    )
    val coinsLabel = stringResource(R.string.stat_coins)
    val bestLabel = stringResource(R.string.stat_best)
    val cards = mutableListOf<Triple<String, String, Color>>()
    cards.add(Triple(ordersLabel, ordersValue, SpicePrimary))
    if (state.coins > 0) {
        cards.add(Triple(coinsLabel, formatNumber(state.coins), SpiceAccent))
    }
    if (state.bestScore > 0) {
        cards.add(Triple(bestLabel, formatNumber(state.bestScore), SpiceTerracotta))
    }
    if (cards.size < 2) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        cards.forEach { card ->
            StatCard(
                label = card.first,
                value = card.second,
                accent = card.third,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
