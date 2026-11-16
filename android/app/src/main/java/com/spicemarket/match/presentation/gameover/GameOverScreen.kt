package com.spicemarket.match.presentation.gameover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicemarket.match.R
import com.spicemarket.match.core.di.AppViewModelFactory
import com.spicemarket.match.core.theme.SpiceAccent
import com.spicemarket.match.core.theme.SpiceBackground
import com.spicemarket.match.core.theme.SpicePrimary
import com.spicemarket.match.core.theme.SpiceSage
import com.spicemarket.match.core.theme.SpiceSurfaceVariant
import com.spicemarket.match.core.theme.SpiceTerracotta
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.core.ui.AppBackground
import com.spicemarket.match.core.ui.PrimaryButton
import com.spicemarket.match.core.ui.SecondaryButton
import com.spicemarket.match.core.ui.SpiceJar
import com.spicemarket.match.core.ui.StatCard
import com.spicemarket.match.core.ui.spiceNameRes
import com.spicemarket.match.core.util.formatNumber
import com.spicemarket.match.domain.model.OrderLine
import com.spicemarket.match.domain.model.RoundResult

@Composable
fun GameOverScreen(
    result: RoundResult,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory)
    val state by gameOverViewModel.uiState.collectAsStateWithLifecycle()
    GameOverContent(
        result = result,
        state = state,
        onPlayAgain = onPlayAgain,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    result: RoundResult,
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val cardState = remember { MutableTransitionState(false) }
    cardState.targetState = true
    val coins by animateIntAsState(
        targetValue = result.coinsEarned,
        animationSpec = tween(if (state.reducedMotion) 120 else 700),
        label = "coins"
    )

    AppBackground(
        overlay = Brush.verticalGradient(
            listOf(
                SpicePrimary.copy(alpha = 0.82f),
                SpiceTextPrimary.copy(alpha = 0.88f)
            )
        )
    ) {
        Spacer(modifier = Modifier.weight(1f))
        AnimatedVisibility(
            visibleState = cardState,
            enter = scaleIn(spring(dampingRatio = 0.68f, stiffness = 300f)) + fadeIn(tween(260))
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.88f),
                shape = RoundedCornerShape(24.dp),
                color = SpiceBackground,
                border = BorderStroke(1.dp, SpiceSurfaceVariant),
                shadowElevation = 18.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AwningBand()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (result.win) {
                            stringResource(R.string.result_win)
                        } else {
                            stringResource(R.string.result_lose)
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (result.win) SpiceSage else SpicePrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    OrderRecap(lines = result.lines)
                    Spacer(modifier = Modifier.height(18.dp))
                    ResultStats(result = result, coins = coins, best = state.bestScore)
                    Spacer(modifier = Modifier.height(20.dp))
                    PrimaryButton(
                        text = stringResource(R.string.play_again),
                        onClick = onPlayAgain,
                        icon = Icons.Default.Refresh,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SecondaryButton(
                        text = stringResource(R.string.menu),
                        onClick = onMenu,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AwningBand() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        repeat(10) { slot ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .background(if (slot % 2 == 0) SpicePrimary else SpiceSurfaceVariant)
            )
        }
    }
}

@Composable
private fun OrderRecap(lines: List<OrderLine>) {
    if (lines.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        lines.forEach { line ->
            val name = stringResource(spiceNameRes(line.spice))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                SpiceJar(
                    spice = line.spice,
                    size = 44.dp,
                    label = stringResource(R.string.cd_jar, name),
                    showSlot = false
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            R.string.order_progress,
                            line.cleared,
                            line.target
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (line.filled) SpiceSage else SpicePrimary
                    )
                    if (line.filled) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.titleMedium,
                            color = SpiceSage
                        )
                    }
                }
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall,
                    color = SpiceTextPrimary.copy(alpha = if (line.filled) 0.6f else 0.45f)
                )
            }
        }
    }
}

@Composable
private fun ResultStats(result: RoundResult, coins: Int, best: Int) {
    val cards = mutableListOf<Triple<String, String, Color>>()
    if (coins > 0) {
        cards.add(
            Triple(
                stringResource(R.string.result_coins),
                stringResource(R.string.plus_value, formatNumber(coins)),
                SpiceAccent
            )
        )
    }
    if (result.stallPoints > 0) {
        cards.add(
            Triple(
                stringResource(R.string.stat_points),
                stringResource(R.string.plus_value, formatNumber(result.stallPoints)),
                SpiceSage
            )
        )
    }
    if (result.score > 0) {
        cards.add(
            Triple(
                stringResource(R.string.stat_score),
                formatNumber(result.score),
                SpicePrimary
            )
        )
    }
    if (cards.size < 2 && best > 0) {
        cards.add(Triple(stringResource(R.string.stat_best), formatNumber(best), SpiceTerracotta))
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
