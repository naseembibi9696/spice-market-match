package com.spicemarket.match.presentation.game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import com.spicemarket.match.core.theme.SpiceSurfaceVariant
import com.spicemarket.match.core.theme.SpiceTerracotta
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.core.ui.OrderCard
import com.spicemarket.match.core.ui.PrimaryButton
import com.spicemarket.match.core.ui.SecondaryButton
import com.spicemarket.match.core.ui.SpiceJar
import com.spicemarket.match.core.ui.spiceNameRes
import com.spicemarket.match.core.util.formatNumber
import com.spicemarket.match.domain.model.RoundResult
import com.spicemarket.match.presentation.dialog.PauseDialog
import kotlin.math.floor

@Composable
fun GameScreen(
    roundId: Int,
    onRoundFinished: (RoundResult) -> Unit,
    onMenu: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = AppViewModelFactory)
    val state by gameViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(roundId) {
        gameViewModel.openRound(roundId)
    }
    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.RoundFinished -> onRoundFinished(event.result)
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    GameContent(
        state = state,
        onCellTap = { index -> gameViewModel.onCellTap(index) },
        onShuffle = { gameViewModel.shuffle() },
        onHint = { gameViewModel.hint() },
        onPause = { gameViewModel.openPause() },
        onResume = { gameViewModel.closePause() },
        onRestart = {
            gameViewModel.closePause()
            gameViewModel.restart()
        },
        onMenu = onMenu
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onCellTap: (Int) -> Unit,
    onShuffle: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            SpiceBackground.copy(alpha = 0.90f),
                            SpiceBackground.copy(alpha = 0.97f),
                            SpiceSurfaceVariant.copy(alpha = 0.92f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHeader(stall = state.stall, onMenu = onMenu, onPause = onPause)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                SpiceBoard(
                    state = state,
                    availableWidth = maxWidth,
                    availableHeight = maxHeight,
                    onCellTap = onCellTap
                )
            }
            OrderStrip(state = state)
            Spacer(modifier = Modifier.height(12.dp))
            ControlsBlock(state = state, onShuffle = onShuffle, onHint = onHint)
        }
        if (state.paused) {
            PauseDialog(
                onResume = onResume,
                onRestart = onRestart,
                onQuit = onMenu
            )
        }
    }
}

@Composable
private fun GameHeader(stall: Int, onMenu: () -> Unit, onPause: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        color = SpicePrimary,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIcon(
                drawable = R.drawable.icon_home,
                label = stringResource(R.string.cd_home),
                onClick = onMenu
            )
            Text(
                text = stringResource(R.string.game_stall_title, stall),
                style = MaterialTheme.typography.titleMedium,
                color = SpiceOnDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            HeaderIcon(
                drawable = R.drawable.icon_pause,
                label = stringResource(R.string.cd_pause),
                onClick = onPause
            )
        }
    }
}

@Composable
private fun HeaderIcon(drawable: Int, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = label,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun SpiceBoard(
    state: GameUiState,
    availableWidth: Dp,
    availableHeight: Dp,
    onCellTap: (Int) -> Unit
) {
    val frame = 8.dp
    val boardMax = (availableWidth - 32.dp).coerceAtMost(364.dp)
    val cellByWidth = floor((boardMax - frame * 2).value / state.cols)
    val cellByHeight = floor((availableHeight - 16.dp - frame * 2).value / state.rows)
    val cell = minOf(cellByWidth, cellByHeight).coerceAtLeast(24f).dp
    val boardWidth = cell * state.cols + frame * 2
    val boardHeight = cell * state.rows + frame * 2
    val sweep = rememberSweep(state.reducedMotion)
    val boardLabel = stringResource(R.string.cd_board, state.cols, state.rows)
    val selectedLabel = stringResource(R.string.state_selected)
    val specialLabel = stringResource(R.string.state_special)
    val plainLabel = stringResource(R.string.state_plain)
    val motion = if (state.reducedMotion) {
        GameConfig.REDUCED_MOTION_MS
    } else {
        GameConfig.CLEAR_MS.toInt()
    }

    Surface(
        modifier = Modifier
            .size(width = boardWidth, height = boardHeight)
            .semantics { contentDescription = boardLabel },
        shape = RoundedCornerShape(18.dp),
        color = SpiceSurfaceVariant,
        border = BorderStroke(2.dp, SpicePrimary.copy(alpha = 0.55f)),
        shadowElevation = 14.dp
    ) {
        Column(modifier = Modifier.padding(frame)) {
            for (row in 0 until state.rows) {
                Row {
                    for (col in 0 until state.cols) {
                        val index = row * state.cols + col
                        val jar = state.board.getOrNull(index)
                        if (jar == null) {
                            Spacer(modifier = Modifier.size(cell))
                        } else {
                            val name = stringResource(spiceNameRes(jar.spice))
                            SpiceJar(
                                spice = jar.spice,
                                size = cell,
                                label = stringResource(R.string.cd_jar, name),
                                stateLabel = when {
                                    jar.special -> specialLabel
                                    state.selected == index -> selectedLabel
                                    else -> plainLabel
                                },
                                selected = state.selected == index,
                                special = jar.special,
                                clearing = state.clearing.contains(index),
                                invalid = state.invalid.contains(index),
                                hinted = state.hinted.contains(index),
                                sweepAngle = sweep,
                                motionMillis = motion,
                                onClick = { onCellTap(index) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberSweep(reducedMotion: Boolean): Float {
    if (reducedMotion) return 0f
    val transition = rememberInfiniteTransition(label = "sweep")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "sweepAngle"
    )
    return angle
}

@Composable
private fun OrderStrip(state: GameUiState) {
    val labels = state.orderLines.map { stringResource(spiceNameRes(it.spice)) }
    val progress = state.orderLines.map {
        stringResource(R.string.order_progress, it.cleared, it.target)
    }
    OrderCard(
        lines = state.orderLines,
        labels = labels,
        progressLabels = progress,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun ControlsBlock(
    state: GameUiState,
    onShuffle: () -> Unit,
    onHint: () -> Unit
) {
    val lowMoves = state.movesLeft <= 5
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        color = SpiceSurfaceVariant.copy(alpha = 0.75f),
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.moves_left),
                        style = MaterialTheme.typography.labelSmall,
                        color = SpiceTextPrimary.copy(alpha = 0.65f)
                    )
                    Text(
                        text = formatNumber(state.movesLeft),
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (lowMoves) SpiceTerracotta else SpicePrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SpiceBackground,
                    border = BorderStroke(1.dp, SpiceAccent.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.stat_score),
                            style = MaterialTheme.typography.labelSmall,
                            color = SpiceTextPrimary.copy(alpha = 0.55f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatNumber(state.score),
                            style = MaterialTheme.typography.headlineSmall,
                            color = SpiceAccent
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.shuffle),
                    onClick = onShuffle,
                    enabled = state.interactive && state.movesLeft > 0,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = stringResource(R.string.hint_count, state.hintsLeft),
                    onClick = onHint,
                    enabled = state.interactive && state.hintsLeft > 0,
                    height = 48.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
