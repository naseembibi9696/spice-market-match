package com.spicemarket.match.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spicemarket.match.core.theme.SpiceAccent
import com.spicemarket.match.core.theme.SpiceBackground
import com.spicemarket.match.core.theme.SpiceOnDark
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.core.util.formatNumber

@Composable
fun CoinBadge(
    coins: Int,
    modifier: Modifier = Modifier,
    onDark: Boolean = true
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (onDark) SpiceTextPrimary.copy(alpha = 0.55f) else SpiceBackground,
        border = BorderStroke(1.dp, SpiceAccent.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = SpiceAccent,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = formatNumber(coins),
                style = MaterialTheme.typography.titleMedium,
                color = if (onDark) SpiceOnDark else SpiceTextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
