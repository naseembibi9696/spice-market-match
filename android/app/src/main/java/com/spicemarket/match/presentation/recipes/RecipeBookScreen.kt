package com.spicemarket.match.presentation.recipes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
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
import com.spicemarket.match.core.theme.SpiceSurfaceVariant
import com.spicemarket.match.core.theme.SpiceTextPrimary
import com.spicemarket.match.core.ui.AppBackground
import com.spicemarket.match.core.ui.CoinBadge
import com.spicemarket.match.core.ui.SecondaryButton
import com.spicemarket.match.core.ui.SectionTitle
import com.spicemarket.match.core.ui.SpiceJar
import com.spicemarket.match.core.ui.spiceFactRes
import com.spicemarket.match.core.ui.spiceNameRes
import com.spicemarket.match.core.util.formatNumber

@Composable
fun RecipeBookScreen(onBack: () -> Unit) {
    val recipeViewModel: RecipeBookViewModel = viewModel(factory = AppViewModelFactory)
    val state by recipeViewModel.uiState.collectAsStateWithLifecycle()
    RecipeBookContent(state = state, onBack = onBack)
}

@Composable
private fun RecipeBookContent(state: RecipeBookUiState, onBack: () -> Unit) {
    AppBackground(
        overlay = Brush.verticalGradient(
            listOf(
                SpiceTextPrimary.copy(alpha = 0.92f),
                SpicePrimaryDeep.copy(alpha = 0.86f),
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
                title = stringResource(R.string.recipes_title),
                subtitle = stringResource(R.string.recipes_subtitle),
                modifier = Modifier.weight(1f)
            )
            CoinBadge(coins = state.coins)
        }
        if (state.empty) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.recipes_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SpiceSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.entries, key = { it.spice.name }) { entry ->
                    RecipeRow(entry = entry)
                }
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
private fun RecipeRow(entry: RecipeEntry) {
    val name = stringResource(spiceNameRes(entry.spice))
    val stateLabel = if (entry.unlocked) {
        stringResource(R.string.state_unlocked)
    } else {
        stringResource(R.string.state_locked)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { stateDescription = stateLabel },
        shape = RoundedCornerShape(16.dp),
        color = SpiceBackground,
        border = BorderStroke(1.dp, SpiceSurfaceVariant),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.graphicsLayer { alpha = if (entry.unlocked) 1f else 0.3f }
            ) {
                SpiceJar(
                    spice = entry.spice,
                    size = 56.dp,
                    label = stringResource(R.string.cd_jar, name),
                    showSlot = false
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = SpicePrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (entry.unlocked) {
                        stringResource(spiceFactRes(entry.spice))
                    } else {
                        stringResource(R.string.recipes_locked)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SpiceTextPrimary.copy(alpha = 0.7f)
                )
                if (entry.unlocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SpiceAccent.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, SpiceAccent.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = stringResource(
                                R.string.matched_count,
                                formatNumber(entry.matched)
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = SpicePrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}
