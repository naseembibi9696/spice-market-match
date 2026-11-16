package com.spicemarket.match.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicemarket.match.R
import com.spicemarket.match.core.di.AppViewModelFactory
import com.spicemarket.match.core.theme.SpiceAccent
import com.spicemarket.match.core.theme.SpiceBackground
import com.spicemarket.match.core.theme.SpiceOnDark
import com.spicemarket.match.core.theme.SpicePrimary
import com.spicemarket.match.core.theme.SpiceSurfaceVariant
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.core.ui.AppBackground

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splash")
    val halo by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
        label = "halo"
    )
    val breath by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "breath"
    )
    val moteFirst by transition.animateFloat(
        initialValue = 0f,
        targetValue = -36f,
        animationSpec = infiniteRepeatable(tween(3200), RepeatMode.Reverse),
        label = "moteFirst"
    )
    val moteSecond by transition.animateFloat(
        initialValue = 0f,
        targetValue = -28f,
        animationSpec = infiniteRepeatable(tween(3800), RepeatMode.Reverse),
        label = "moteSecond"
    )
    val moteThird by transition.animateFloat(
        initialValue = 0f,
        targetValue = -42f,
        animationSpec = infiniteRepeatable(tween(4400), RepeatMode.Reverse),
        label = "moteThird"
    )

    val medallionState = remember { MutableTransitionState(false) }
    medallionState.targetState = true

    AppBackground(
        overlay = Brush.verticalGradient(
            listOf(
                SpiceTextPrimary.copy(alpha = 0.94f),
                SpicePrimary.copy(alpha = 0.90f),
                SpiceTextPrimary.copy(alpha = 0.96f)
            )
        )
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                SpiceAccent.copy(alpha = halo),
                                SpicePrimary.copy(alpha = 0f)
                            )
                        ),
                        shape = CircleShape
                    )
            )
            Medallion(visibleState = medallionState, breath = breath)
        }
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(
            visibleState = medallionState,
            enter = fadeIn(tween(520, delayMillis = 180)) +
                slideInVertically(tween(520, delayMillis = 180)) { full -> full / 6 }
        ) {
            Text(
                text = stringResource(R.string.splash_title),
                style = MaterialTheme.typography.displaySmall,
                color = SpiceOnDark,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.splash_subtitle),
            style = MaterialTheme.typography.labelLarge,
            color = SpiceSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        LinearProgressIndicator(
            color = SpiceAccent,
            trackColor = SpiceSurfaceVariant.copy(alpha = 0.25f),
            modifier = Modifier
                .width(176.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.splash_loading),
            style = MaterialTheme.typography.labelSmall,
            color = SpiceSurfaceVariant
        )
        Spacer(modifier = Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            Mote(offset = moteFirst)
            Spacer(modifier = Modifier.width(18.dp))
            Mote(offset = moteSecond)
            Spacer(modifier = Modifier.width(18.dp))
            Mote(offset = moteThird)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun Medallion(visibleState: MutableTransitionState<Boolean>, breath: Float) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = scaleIn(spring(dampingRatio = 0.62f, stiffness = 260f)) + fadeIn(tween(420))
    ) {
        Surface(
            modifier = Modifier.size(208.dp),
            shape = CircleShape,
            color = SpiceBackground.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, SpiceSurfaceVariant.copy(alpha = 0.35f)),
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(168.dp)
                        .graphicsLayer {
                            scaleX = breath
                            scaleY = breath
                        }
                )
            }
        }
    }
}

@Composable
private fun Mote(offset: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .graphicsLayer { translationY = offset }
            .background(SpiceAccent.copy(alpha = 0.5f), CircleShape)
    )
}
