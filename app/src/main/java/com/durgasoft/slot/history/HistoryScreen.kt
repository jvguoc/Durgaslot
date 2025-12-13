@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.durgasoft.slot.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.durgasoft.slot.R
import com.durgasoft.slot.data.ScoreEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val vm: HistoryVM = viewModel(factory = HistoryVMFactory(ctx))
    val ui = vm.ui

    var cleared by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { cleared = true }
                    ) {
                        Text(stringResource(R.string.history_clear))
                    }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            when {
                ui.loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                ui.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ui.error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    val visibleScores: List<ScoreEntity> =
                        if (cleared) {
                            emptyList()
                        } else {
                            ui.scores.sortedByDescending { it.savedAtMillis }
                        }

                    if (visibleScores.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.history_empty_visible))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(visibleScores) { score ->
                                HistoryItem(score = score)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(score: ScoreEntity) {
    val dateText = rememberDate(score.savedAtMillis)
    val locText = score.location ?: "Desconocida"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.history_item_score, score.maxChips),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(R.string.history_item_date, dateText),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.history_item_location, locText),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun rememberDate(millis: Long): String {
    val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return df.format(Date(millis))
}
