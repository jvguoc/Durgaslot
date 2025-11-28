@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.durgasoft.slot.history

import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de partidas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al menú"
                        )
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
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                ui.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(text = ui.error, color = MaterialTheme.colorScheme.error)
                    }
                }

                else -> {
                    if (ui.scores.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text("No hay partidas guardadas todavía.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ui.scores) { score ->
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
private fun HistoryItem(score: com.durgasoft.slot.data.ScoreEntity) {
    val dateText = rememberDate(score.savedAtMillis)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Puntuación máxima: ${score.maxChips}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Fecha: $dateText",
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
