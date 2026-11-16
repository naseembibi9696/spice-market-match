package com.spicemarket.match.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.spicemarket.match.core.theme.SpiceBackground
import com.spicemarket.match.core.theme.SpicePrimary
import com.spicemarket.match.core.theme.SpiceSage
import com.spicemarket.match.core.theme.SpiceSurfaceVariant
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.domain.model.OrderLine

@Composable
fun OrderCard(
    lines: List<OrderLine>,
    labels: List<String>,
    progressLabels: List<String>,
    modifier: Modifier = Modifier
) {
    if (lines.isEmpty()) return
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = RoundedCornerShape(16.dp),
        color = SpiceBackground,
        border = BorderStroke(1.dp, SpiceSurfaceVariant),
        shadowElevation = 6.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(SpiceSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SpiceSurfaceVariant.copy(alpha = 0.6f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                lines.forEachIndexed { index, line ->
                    OrderLineItem(
                        line = line,
                        label = labels.getOrElse(index) { "" },
                        progress = progressLabels.getOrElse(index) { "" }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderLineItem(
    line: OrderLine,
    label: String,
    progress: String
) {
    val bounce by animateFloatAsState(
        targetValue = if (line.filled) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 520f),
        label = "orderBounce"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.graphicsLayer {
            scaleX = bounce
            scaleY = bounce
        }
    ) {
        SpiceJar(
            spice = line.spice,
            size = 34.dp,
            label = label,
            showSlot = false,
            modifier = Modifier.size(34.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = progress,
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
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = SpiceTextPrimary.copy(alpha = 0.6f)
            )
        }
    }
}
