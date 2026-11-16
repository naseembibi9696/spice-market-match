package com.spicemarket.match.presentation.decor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.spicemarket.match.core.di.AppViewModelFactory
import com.spicemarket.match.core.theme.SpiceAccent
import com.spicemarket.match.core.theme.SpiceBackground
import com.spicemarket.match.core.theme.SpicePrimary
import com.spicemarket.match.core.theme.SpicePrimaryDeep
import com.spicemarket.match.core.theme.SpiceSage
import com.spicemarket.match.core.theme.SpiceSurfaceVariant
import com.spicemarket.match.core.theme.SpiceTerracotta
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.core.ui.AppBackground
import com.spicemarket.match.core.ui.CoinBadge
import com.spicemarket.match.core.ui.PrimaryButton
import com.spicemarket.match.core.ui.SecondaryButton
import com.spicemarket.match.core.ui.SectionTitle
import com.spicemarket.match.core.ui.decorNameRes
import com.spicemarket.match.core.util.formatNumber

@Composable
fun StallDecorScreen(onBack: () -> Unit) {
    val decorViewModel: StallDecorViewModel = viewModel(factory = AppViewModelFactory)
    val state by decorViewModel.uiState.collectAsStateWithLifecycle()
    StallDecorContent(
        state = state,
        onBuy = { id -> decorViewModel.buy(id) },
        onPlace = { id -> decorViewModel.place(id) },
        onBack = onBack
    )
}

@Composable
private fun StallDecorContent(
    state: StallDecorUiState,
    onBuy: (String) -> Unit,
    onPlace: (String) -> Unit,
    onBack: () -> Unit
) {
    AppBackground(
        overlay = Brush.verticalGradient(
            listOf(
                SpiceTextPrimary.copy(alpha = 0.90f),
                SpicePrimaryDeep.copy(alpha = 0.88f),
                SpiceTextPrimary.copy(alpha = 0.94f)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(
                title = stringResource(R.string.decor_title),
                subtitle = stringResource(R.string.decor_subtitle),
                modifier = Modifier.weight(1f)
            )
            CoinBadge(coins = state.coins)
        }
        StallPreview(state = state)
        Spacer(modifier = Modifier.height(14.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.entries, key = { it.id }) { entry ->
                DecorCard(entry = entry, onBuy = onBuy, onPlace = onPlace)
            }
        }
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
}

@Composable
private fun StallPreview(state: StallDecorUiState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(110.dp),
        shape = RoundedCornerShape(18.dp),
        color = SpiceBackground,
        border = BorderStroke(1.dp, SpiceSurfaceVariant),
        shadowElevation = 10.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .background(decorBrush(state.placedAwning))
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp, start = 18.dp, end = 18.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_preview),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(decorBrush(state.placedLamp), RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

@Composable
private fun DecorCard(
    entry: DecorEntry,
    onBuy: (String) -> Unit,
    onPlace: (String) -> Unit
) {
    val stateLabel = when {
        entry.placed -> stringResource(R.string.state_placed)
        entry.owned -> stringResource(R.string.state_owned)
        else -> stringResource(R.string.state_locked)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { stateDescription = stateLabel },
        shape = RoundedCornerShape(16.dp),
        color = SpiceBackground.copy(alpha = if (entry.owned || entry.affordable) 1f else 0.6f),
        border = BorderStroke(
            1.dp,
            if (entry.placed) SpiceSage else SpiceSurfaceVariant
        ),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .background(decorBrush(entry.id), RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(decorNameRes(entry.id)),
                style = MaterialTheme.typography.titleMedium,
                color = SpiceTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            when {
                entry.placed -> SecondaryButton(
                    text = stringResource(R.string.decor_placed),
                    onClick = { onPlace(entry.id) },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                entry.owned -> PrimaryButton(
                    text = stringResource(R.string.decor_place),
                    onClick = { onPlace(entry.id) },
                    height = 48.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                else -> PrimaryButton(
                    text = stringResource(R.string.decor_buy, formatNumber(entry.price)),
                    onClick = { onBuy(entry.id) },
                    enabled = entry.affordable,
                    height = 48.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun decorBrush(id: String?): Brush = when (id) {
    "striped_awning" -> Brush.horizontalGradient(
        listOf(SpicePrimary, SpiceBackground, SpicePrimary, SpiceBackground, SpicePrimary)
    )
    "lace_awning" -> Brush.horizontalGradient(
        listOf(SpiceBackground, SpiceSurfaceVariant, SpiceBackground, SpiceSurfaceVariant)
    )
    "brass_lamp" -> Brush.verticalGradient(listOf(SpiceTerracotta, SpiceSurfaceVariant))
    "paper_lantern" -> Brush.verticalGradient(listOf(SpiceSage, SpiceBackground))
    "rope_garland" -> Brush.horizontalGradient(listOf(SpiceSurfaceVariant, SpiceAccent))
    "spice_sack" -> Brush.verticalGradient(listOf(SpicePrimaryDeep, SpiceTerracotta))
    else -> Brush.horizontalGradient(listOf(SpiceSurfaceVariant, SpiceBackground))
}
