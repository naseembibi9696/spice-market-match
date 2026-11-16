package com.spicemarket.match.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spicemarket.match.R
import com.spicemarket.match.core.theme.SpiceAccent
import com.spicemarket.match.core.theme.SpiceBackground
import com.spicemarket.match.core.theme.SpicePrimary
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.domain.model.Spice

fun spiceDrawable(spice: Spice): Int = when (spice) {
    Spice.SAFFRON -> R.drawable.sprite_1
    Spice.PAPRIKA -> R.drawable.sprite_2
    Spice.CINNAMON -> R.drawable.sprite_3
    Spice.CUMIN -> R.drawable.sprite_4
    Spice.CARDAMOM -> R.drawable.sprite_5
}

@Composable
fun SpiceJar(
    spice: Spice,
    size: Dp,
    label: String,
    modifier: Modifier = Modifier,
    stateLabel: String? = null,
    selected: Boolean = false,
    special: Boolean = false,
    clearing: Boolean = false,
    invalid: Boolean = false,
    hinted: Boolean = false,
    showSlot: Boolean = true,
    sweepAngle: Float = 0f,
    motionMillis: Int = 220,
    onClick: (() -> Unit)? = null
) {
    val scale by animateFloatAsState(
        targetValue = when {
            clearing -> 0f
            selected -> 1.08f
            else -> 1f
        },
        animationSpec = tween(motionMillis),
        label = "jarScale"
    )
    val tint by animateFloatAsState(
        targetValue = if (invalid) 0.25f else 0f,
        animationSpec = tween(motionMillis),
        label = "jarTint"
    )
    val slotShape = RoundedCornerShape(12.dp)
    var container = modifier.size(size)
    if (onClick != null) {
        container = container.clickable(onClick = onClick)
    }
    Box(
        modifier = container
            .padding(3.dp)
            .semantics {
                contentDescription = label
                if (stateLabel != null) stateDescription = stateLabel
            },
        contentAlignment = Alignment.Center
    ) {
        if (showSlot) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (selected) {
                            SpiceAccent.copy(alpha = 0.22f)
                        } else {
                            SpiceBackground.copy(alpha = 0.55f)
                        },
                        shape = slotShape
                    )
                    .border(
                        width = if (selected || hinted) 2.dp else 1.dp,
                        color = when {
                            selected -> SpiceAccent
                            hinted -> SpiceAccent.copy(alpha = 0.7f)
                            else -> SpiceTextPrimary.copy(alpha = 0.08f)
                        },
                        shape = slotShape
                    )
            )
        }
        if (special) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationZ = sweepAngle }
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(SpicePrimary, SpiceAccent, SpicePrimary)
                        ),
                        shape = slotShape
                    )
            )
        }
        Image(
            painter = painterResource(spiceDrawable(spice)),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = if (clearing) scale else 1f
                }
        )
        if (tint > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SpicePrimary.copy(alpha = tint), slotShape)
            )
        }
    }
}
